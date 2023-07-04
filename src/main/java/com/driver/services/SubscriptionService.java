package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();

        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());

        int amount = 0;
        if(subscription.getSubscriptionType().toString().equals("BASIC")){
            amount = 500 + (200 * subscription.getNoOfScreensSubscribed());
        } else if (subscription.getSubscriptionType().toString().equals("PRO")) {
            amount = 800 + (250 * subscription.getNoOfScreensSubscribed());
        }else {
            amount = 1000 * (350 * subscription.getNoOfScreensSubscribed());
        }

        subscription.setTotalAmountPaid(amount);
        subscription.setUser(user);
        user.setSubscription(subscription);

        userRepository.save(user);

        return amount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();
        Subscription userSubscription = user.getSubscription();
        if(userSubscription.getSubscriptionType().toString().equals("ELITE")){
            throw new Exception("Already the best Subscription");
        }

        int prevSubscriptionPaid = userSubscription.getTotalAmountPaid();
        int currPaid = 0;

        if(userSubscription.getSubscriptionType().toString().equals("BASIC")){
            currPaid = 800 + (250 * userSubscription.getNoOfScreensSubscribed());
            userSubscription.setSubscriptionType(SubscriptionType.PRO);
        }else{
            currPaid = 1000 + (350 * userSubscription.getNoOfScreensSubscribed());
            userSubscription.setSubscriptionType(SubscriptionType.ELITE);
        }

        subscriptionRepository.save(userSubscription);

        return currPaid - prevSubscriptionPaid;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subscriptionList = subscriptionRepository.findAll();

        int revenue = 0;
        for(Subscription subscription : subscriptionList){
            revenue += subscription.getTotalAmountPaid();
        }

        return revenue;
    }

}
