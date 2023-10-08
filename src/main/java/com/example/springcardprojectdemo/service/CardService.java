package com.example.springcardprojectdemo.service;

import com.example.springcardprojectdemo.entity.*;
import com.example.springcardprojectdemo.payload.ApiResponse;
import com.example.springcardprojectdemo.payload.CardDto;
import com.example.springcardprojectdemo.payload.TransferDto;
import com.example.springcardprojectdemo.repository.*;
import com.example.springcardprojectdemo.security.JwtFilter;
import com.example.springcardprojectdemo.security.JwtFilter2;
import com.example.springcardprojectdemo.utills.CommonUtills;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CardService {

    @Autowired
    CardRepository cardRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    HistoriesRepository historiesRepository;

    @Autowired
    MailService mailService;

    @Autowired
    VerifyRepository verifyRepository;

    @Autowired
    TokenRepository tokenRepository;

    int max_card = 5;

    public ApiResponse getAllCardsForUser() {
        String token = JwtFilter.getToken;
        Optional<Token> token_details = tokenRepository.findByToken(token);
        if (token_details.get().getLevel().equals("valid")) {
            String email = JwtFilter.getEmailWithToken;
            List<Card> cards = userRepository.findByEmail(email).get().getCards();
            System.out.println(cards);
            if (cards == null) {
                return new ApiResponse("null", false);
            }
            return new ApiResponse("Cards", true, cards);
        }
        return new ApiResponse("token failed", false);
    }


    public ApiResponse createCard(CardDto dto) {
        String token = JwtFilter.getToken;
        Optional<Token> token_details = tokenRepository.findByToken(token);
        if (token_details.get().getLevel().equals("valid")) {
            Card newCard = new Card();
            User user = userRepository.findByEmail(JwtFilter.getEmailWithToken).get();

            newCard.setCompany(dto.getCompany());
            newCard.setType(dto.getTypee());
            newCard.setCode(dto.getCode());
            newCard.setExp_date("01/27");
            newCard.setBalance(dto.getTypee().equals("Visa") ? 100 : 1000000);
            newCard.setUnit(dto.getTypee().equals("Visa") ? "$" : "uzs");
            newCard.setNumber(checkCardNumberWithOverNumbers());
            newCard.setOwner(user.getFirstName() + " " + user.getLastName());
            try {
                if (user.getCards().size() < max_card) {
                    Card saved_card = cardRepository.save(newCard);
                    cardRepository.addCardToUserWithId(user.getId(), saved_card.getId());
                    return new ApiResponse("success", true);
                } else return new ApiResponse("Limit is over. Max card counts is " + max_card, false);
            } catch (Exception e) {
                return new ApiResponse("failed", false);
            }
        }
        return new ApiResponse("token failed", false);
    }

    public ApiResponse v_transfer(TransferDto dto) {

        String token = JwtFilter.getToken;
        Optional<Token> token_details = tokenRepository.findByToken(token);
        if (token_details.get().getLevel().equals("valid")) {
            String sender_email = cardRepository.getEmailWithCardNumber(dto.getSender_card_number());
            if (dto.getCode() == -1) {
                int tra_code = CommonUtills.generateCode();
                verifyRepository.save(new Verify((long) tra_code, sender_email));
                mailService.sendTextt(sender_email, "transfer code is: " + tra_code);
                return new ApiResponse("transfer code is sending", true);
            } else if (dto.getCode() != verifyRepository.findByEmail(sender_email).get().getVerifyCode()) {
                return new ApiResponse("transfer code is incorrect", false);
            } else {
                return transfer(dto, sender_email);
            }
        }
        return new ApiResponse("token failed", false);
    }

    public ApiResponse transfer(TransferDto dto, String sender_email) {

        String token = JwtFilter.getToken;
        Optional<Token> token_details = tokenRepository.findByToken(token);
        if (token_details.get().getLevel().equals("valid")) {
            verifyRepository.deleteByEmail(sender_email);
            Optional<Card> payee = cardRepository.findByCardNumber(dto.getTrans_card_number());
            if (payee.isPresent()) {
                Card sender = cardRepository.findByCardNumber(dto.getSender_card_number()).get();
                if (sender.getBalance() >= dto.getTrans_sum()) {
                    if (sender.getUnit().equals(payee.get().getUnit())) {
                        cardRepository.plusMoney(payee.get().getNumber(), dto.getTrans_sum());
                        cardRepository.minusMoney(sender.getNumber(), dto.getTrans_sum());
                    } else {
                        if (sender.getUnit().equals("uzs") && payee.get().getUnit().equals("$")) {
                            cardRepository.plusMoney(payee.get().getNumber(), convertMoneyToUsa(dto.getTrans_sum()));
                            cardRepository.minusMoney(sender.getNumber(), dto.getTrans_sum());
                        } else {
                            cardRepository.plusMoney(payee.get().getNumber(), convertMoneyToUzs(dto.getTrans_sum()));
                            cardRepository.minusMoney(sender.getNumber(), dto.getTrans_sum());
                        }
                    }
                    System.out.println("--------" + sender.getId() + "----------" + payee.get().getId() + "------");
                    History sender_history = addHistory(sender, payee.get(), dto.getTrans_sum(), 0., "sender");
                    History receiver_history = addHistory(sender, payee.get(), dto.getTrans_sum(), 0., "receiver");
                    userRepository.addHistoryToUser(cardRepository.getUserIdByCardId(sender.getId()), sender_history.getId());
                    userRepository.addHistoryToUser(cardRepository.getUserIdByCardId(payee.get().getId()), receiver_history.getId());
                    return new ApiResponse("transfer is successfully", true);
                } else return new ApiResponse("balance is not enough", false);
            } else return new ApiResponse("card not found", false);
        }
        return new ApiResponse("token failed", false);
    }

    public History addHistory(Card sender, Card receiver, double amount, double commission, String condition) {
        try {
            History history = new History();
            history.setSender_name(sender.getOwner());
            history.setSender_card_number(sender.getNumber());
            history.setReceiver_name(receiver.getOwner());
            history.setReceiver_card_number(receiver.getNumber());
            history.setAmount(amount);
            history.setCommission(commission);
            history.setCondition(condition);
            history.setTransaction_date(new Date().getTime());
            history.setSender_bank_name(sender.getCompany());
            history.setReceiver_bank_name(receiver.getCompany());
            return historiesRepository.save(history);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String checkCardNumberWithOverNumbers() {
        Card byNumber;
        int number;
        do {
            number = CommonUtills.generateNumber();
            byNumber = cardRepository.findByOverNumber(String.valueOf(number));
        } while (byNumber != null);
        return "8600312935" + number;
    }

    public int convertMoneyToUzs(int money) {
        return money * 11000;
    }

    public int convertMoneyToUsa(int money) {
        return money / 11000;
    }

    public ApiResponse checkCardNumber(String number) {
        try {
            Optional<Card> card = cardRepository.findByCardNumber(number);
            if (card.isPresent()) {
                return new ApiResponse("card is available", true);
            }
            return new ApiResponse("card not found", false);
        } catch (Exception e) {
            return new ApiResponse(e.getMessage(), false);
        }
    }
}
