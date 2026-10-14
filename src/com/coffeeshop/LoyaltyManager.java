package com.coffeeshop;

import java.io.*;
import java.util.HashMap;

public class LoyaltyManager {

    private static final String FILE_NAME = "customers.txt";

    public static int getOrderCount(String phone) {

        HashMap<String, Integer> customers = loadCustomers();

        if (customers.containsKey(phone)) {
            return customers.get(phone);
        }

        return 0;
    }

    public static boolean isFifthOrder(String phone) {

        int count = getOrderCount(phone);

        return count == 4;
    }

    public static void completeOrder(String phone, boolean redeemed) {

        HashMap<String, Integer> customers = loadCustomers();

        int count = getOrderCount(phone);

        if (redeemed) {
            count = 0;
        } else {
            count++;
        }

        customers.put(phone, count);

        saveCustomers(customers);
    }

    private static HashMap<String, Integer> loadCustomers() {

        HashMap<String, Integer> customers = new HashMap<>();

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return customers;
        }

        try {

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                
                while ((line = reader.readLine()) != null) {
                    
                    String[] parts = line.split(",");
                    
                    if (parts.length == 2) {
                        
                        String phone = parts[0];
                        int count = Integer.parseInt(parts[1]);
                        
                        customers.put(phone, count);
                    }
                }
            }

        } catch (IOException | NumberFormatException e) {

            System.out.println("Error reading customer data.");
        }

        return customers;
    }

    private static void saveCustomers(
            HashMap<String, Integer> customers) {

        try {

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                for (String phone : customers.keySet()) {
                    
                    int count = customers.get(phone);
                    
                    writer.write(phone + "," + count);
                    writer.newLine();
                }
            }

        } catch (IOException e) {

            System.out.println("Error saving customer data.");
        }
    }
}