package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.*;
import org.example.project_cinemas_java.payload.dto.billdtos.BillAdminDTO;
import org.example.project_cinemas_java.payload.dto.billdtos.BillDTO;
import org.example.project_cinemas_java.payload.request.bill_request.CreateBillRequest;
import org.example.project_cinemas_java.payload.request.bill_request.DeleteBillRequest;
import org.example.project_cinemas_java.repository.*;
import org.example.project_cinemas_java.service.iservice.IBillService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class BillService implements IBillService {
    @Autowired
    private BillRepo billRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PromotionRepo promotionRepo;
    @Autowired
    private BillStatusRepo billStatusRepo;

    @Autowired
    private BillTicketRepo billTicketRepo;


    @Autowired
    private BillFoodRepo billFoodRepo;
    @Autowired
    private TicketRepo ticketRepo;


    private String generateCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        return String.valueOf(randomNumber);
    }
    @Override
    public void createBill(String email) throws Exception {
        User user = userRepo.findByEmail(email).orElse(null);
        if(user == null){
            throw new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
        }
        Bill billByUser = billRepo.findBillByUserAndBillstatusId(user,3);

        if( billByUser != null){
            billByUser.setTotalMoney(0);
            billByUser.setTradingCode(generateCode());
            LocalDateTime timeNow = LocalDateTime.now();
            billByUser.setCreateTime(timeNow);
            billByUser.setMonth(timeNow.getMonthValue());
            billByUser.setUser(user);
            billByUser.setName("Bill of"+ user.getName());
            billByUser.setUpdateTime(timeNow);
            billByUser.setPromotion(null);
            billByUser.setBillstatus(billStatusRepo.findById(3).orElse(null));
            billByUser.setActive(true);
            billRepo.save(billByUser);

        }else {
            Bill bill = new Bill();
            bill.setTotalMoney(0);
            bill.setTradingCode(generateCode());
            LocalDateTime timeNow = LocalDateTime.now();
            bill.setCreateTime(timeNow);
            bill.setMonth(timeNow.getMonthValue());
            bill.setUser(user);
            bill.setName("Bill of"+ user.getName());
            bill.setUpdateTime(timeNow);
            bill.setPromotion(null);
            bill.setBillstatus(billStatusRepo.findById(3).orElse(null));
            bill.setActive(true);
            billRepo.save(bill);
        }
    }

    @Override
    public String saveBillInformation(int user) throws Exception {
        User exitstingUser = userRepo.findById(user).orElse(null);
        if(exitstingUser == null){
            throw new DataNotFoundException("Thông tin khách hàng bị lỗi! Thử lại sau ít phút");
        }

        //tìm bill chua thanh toan
        Bill bill = billRepo.findBillByUserAndBillstatusId(exitstingUser,3);
        if(bill == null){
            throw new DataNotFoundException("Không tìm thấy đơn hàng");
        }
        Promotion promotion = bill.getPromotion();
        if(promotion != null) {
            if (promotion.getQuantity() > 0) {
                promotion.setQuantity(promotion.getQuantity() - 1);
                promotionRepo.save(promotion);
            }
        }


        bill.setName(exitstingUser.getUserName() + "đã thanh toán hóa đơn");
        bill.setBillstatus(billStatusRepo.findById(2).orElse(null));
        bill.setUpdateTime(LocalDateTime.now());
        bill.setCreateTime(LocalDateTime.now());
        billRepo.save(bill);

        Set<BillTicket> billTickets = billTicketRepo.findAllByBill(bill);
            if(billTickets.isEmpty()){
            throw new DataNotFoundException("Vui lòng chọn combo");
        }
        List<Ticket> tickets = new ArrayList<>();
        for (BillTicket billTicket: billTickets){
            tickets.add(billTicket.getTicket());
        }
        for (Ticket ticket:tickets){
            ticket.setSeatStatus(4);
            ticketRepo.save(ticket);
        }
        return bill.getTradingCode();
    }

    @Override
    public void resetBillByUser(int userId) throws Exception {
        User user  = userRepo.findById(userId).orElse(null);
        if(user == null) {
            throw new DataNotFoundException("Tài khoản không tồn tại");
        }
        Bill bill = billRepo.findBillByUserAndBillstatusId(user,3);
        if(bill != null){
            Set<BillTicket> billTickets = billTicketRepo.findAllByBill(bill);
            if(billTickets != null){
                for (BillTicket billTicket:billTickets){
                    if(billTicket.getTicket() != null){
                        billTicket.getTicket().setPriceTicket(0);
                        billTicket.getTicket().setUser(null);
                        billTicket.getTicket().setActive(false);
                        billTicket.getTicket().setTicketBookingTime(null);
                        billTicket.getTicket().setCode(null);
                        billTicket.getTicket().setSeatStatus(1);
                        ticketRepo.save(billTicket.getTicket());
                    }
                    billTicket.setBill(null);
                    billTicket.setTicket(null);
                    billTicketRepo.delete(billTicket);
                }
            }
            List<BillFood> billFoods = billFoodRepo.findAllByBill(bill);
            if(billFoods != null){
                for (BillFood billFood:billFoods){
                    billFood.setFood(null);
                    billFood.setBill(null);
                    billFoodRepo.delete(billFood);
                }
            }

            Promotion promotion = bill.getPromotion();
            if(promotion != null){
                promotion.setQuantity(promotion.getQuantity() + 1);
                promotionRepo.save(promotion);
            }
        }
    }

    @Override
    public List<BillAdminDTO> getAllBillAdmin() throws Exception {

        List<BillAdminDTO> billAdminDTOS = new ArrayList<>();
        for (Bill bill:billRepo.findAll()){
            BillAdminDTO billAdminDTO = new BillAdminDTO();
            billAdminDTO.setUser(bill.getUser().getEmail());
            billAdminDTO.setName(bill.getName());
            billAdminDTO.setStatus(bill.getBillstatus().getId());
            billAdminDTO.setMonth(bill.getMonth());
            billAdminDTO.setTotalMoney(bill.getTotalMoney());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime createTime = bill.getCreateTime();
            String formattedDateTime = createTime.format(formatter);
            billAdminDTO.setCreateTime(formattedDateTime);
            billAdminDTO.setVoucher(bill.getPromotion() == null ? null: bill.getPromotion().getName());
            billAdminDTO.setTradingCode(bill.getTradingCode());
            billAdminDTOS.add(billAdminDTO);
        }
        return billAdminDTOS;
    }

    @Override
    public void deleteBill(DeleteBillRequest deleteBillRequest) throws Exception {
        
    }

    public double  getTotalMoney(int user) throws Exception {
        User user1 = userRepo.findById(user).orElse(null);
        if(user1 == null){
            throw new DataNotFoundException("Người dùng không tồn tại");
        }
        Bill bill = billRepo.findBillByUserAndBillstatusId(user1,3);
        if(bill == null){
            throw new DataNotFoundException("Hóa đơn không tồn tại! Vui lòng thử lại");
        }
        return bill.getTotalMoney();
    }


}
