package com.example.Backend.Service.User;

import com.example.Backend.Entity.Follows;
import com.example.Backend.Entity.GroupChat;
import com.example.Backend.Entity.model.User;
import com.example.Backend.Request.GroupChat.RequestCreateGroupChat;
import com.example.Backend.Request.User.*;
import com.example.Backend.Response.ApiResponse.ApiResponse;
import com.sun.xml.txw2.Document;
import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserImpl implements UserService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    JavaMailSender javaMailSender;

    public UserImpl() {
    }


    @Override
    public void createAccount(RequestCreateAccount requestCreateAccount) throws Exception {
        User user = new User();
        user.setEmail(requestCreateAccount.getEmail());
        user.setUsername(requestCreateAccount.getUsername());
        user.setPassword(BCrypt.hashpw(requestCreateAccount.getPassword(), BCrypt.gensalt()));
        user.setName(requestCreateAccount.getName());
        user.setFromGoogle(false);
        mongoTemplate.insert(user, "users");
    }

    @Override
    public ApiResponse<User> loginAccount(RequestLogin requestLogin) throws Exception {
        Query query = new Query(Criteria.where("email").is(requestLogin.getEmail()));
        User user = mongoTemplate.findOne(query, User.class, "users");
        //System.out.println(new Gson().toJson(requestCreateLogin));
        if (requestLogin.isFromGoogle()) {
            if (user == null) {
                User savedUser = new User();
                savedUser.setEmail(requestLogin.getEmail());
                savedUser.setUsername(requestLogin.getUsername());
                savedUser.setAvatarImg(requestLogin.getAvatarImg());
                savedUser.setFromGoogle(true);
                savedUser.setName(requestLogin.getName());
                savedUser.setStatus(true);
                return new ApiResponse<User>(true, "", mongoTemplate.insert(savedUser, "users"));
            } else {
                if (user.isFromGoogle()){
                    user.setStatus(true);
                    mongoTemplate.save(user);
                    return new ApiResponse<User>(true, "", user);
                }
                else
                    return new ApiResponse<User>(false, "Email này đã được đăng ký, vui lòng đăng nhập bằng phương thức khác", null);
            }
        } else {
            if (user == null) return new ApiResponse<User>(false, "Không tìm thấy email này", null);

            boolean matches = BCrypt.checkpw(requestLogin.getPassword(), user.getPassword());
            if (matches) {
                user.setStatus(true);
                mongoTemplate.save(user);
                return new ApiResponse<User>(true, "", user);
            }
            //System.out.println(new Gson().toJson(user));
            return new ApiResponse<User>(false, "Sai mật khẩu", null);
        }
    }

    @Override
    public ApiResponse<String> sendOtp(String email) {
        Query query = new Query(Criteria.where("email").is(email));
        User user = mongoTemplate.findOne(query, User.class, "users");

        if (user != null) {
            return new ApiResponse<String>(false, "Email đã được sử dụng", "");
        }
        String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int OTP_LENGTH = 6;
        StringBuilder otp = new StringBuilder();

        SecureRandom random = new SecureRandom();
        for (int i = 0; i < OTP_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            otp.append(CHARACTERS.charAt(index));
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("OTP Verification");
        message.setText("Your OTP is: " + otp);
       javaMailSender.send(message);
        return new ApiResponse<String>(true, "Mã OTP đã được gửi đến email của bạn", otp.toString());
    }

    @Override
    public ApiResponse<List<String>> getListUserName() {
        // Thực hiện truy vấn để lấy tất cả các username từ cơ sở dữ liệu
        List<User> userList = mongoTemplate.findAll(User.class);

        // Trích xuất các username từ danh sách người dùng và chuyển thành một danh sách mới
        List<String> userNameList = userList.stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
        return new ApiResponse<List<String>>(true, "Success" , userNameList);
    }

    @Override
    public ApiResponse<String> sendOTP_forgotpassword(String email) {
        Query query = new Query(Criteria.where("email").is(email));
        User user = mongoTemplate.findOne(query, User.class, "users");

        String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int OTP_LENGTH = 6;
        StringBuilder otp = new StringBuilder();

        SecureRandom random = new SecureRandom();
        for (int i = 0; i < OTP_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            otp.append(CHARACTERS.charAt(index));
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("OTP Verification");
        message.setText("Your OTP is: " + otp);
        javaMailSender.send(message);
        return new ApiResponse<String>(true, "Mã OTP đã được gửi đến email của bạn" , otp.toString());
    }
    @Override
    public ApiResponse<User> changePW(RequestForgetPass requestForgetPass) {
        Query query = new Query(Criteria.where("email").is(requestForgetPass.getEmail()));
        User user = mongoTemplate.findOne(query, User.class, "users");
        if (user == null) {
            return new ApiResponse<User>(false, "Không tìm thấy ngườ dùng với Email này!",null);
        }
        user.setPassword(BCrypt.hashpw(requestForgetPass.getNewPass(),BCrypt.gensalt()));
        mongoTemplate.save(user,"users");
        return new ApiResponse<>(true, "Đổi mật khẩu thành công!",null);
    }
    @Override
    public ApiResponse<List<User>> getAllUsers() {
        List<User> userList = mongoTemplate.findAll(User.class, "users");
        return new ApiResponse<List<User>>(true, "Lấy tất cả dữ liệu user thành công!", userList);
    }

    @Override
    public ApiResponse<User> changePassword(RequestChangePasword requestChangePass) throws Exception {
        // Tạo query để tìm người dùng dựa trên email
        Query query = new Query(Criteria.where("username").is(requestChangePass.getUsername()));
        // Tìm người dùng trong cơ sở dữ liệu
        User user = mongoTemplate.findOne(query, User.class, "users");
        boolean matches = BCrypt.checkpw(requestChangePass.getCurrentpass(), user.getPassword());
        // Nếu không tìm thấy người dùng, trả về thông báo lỗi
        if (user == null) {
            return new ApiResponse<>(false, "Không tìm thấy người dùng với username này", null);
        }else if(!matches) {
            return new ApiResponse<>(false, "Current password incorrect. Please check and try again.", null);
        }else{
            user.setPassword(BCrypt.hashpw(requestChangePass.getNewpass(), BCrypt.gensalt()));
            // Lưu người dùng đã được cập nhật vào cơ sở dữ liệu
            mongoTemplate.save(user, "users");
            // Trả về thông báo thành cônlg
            return new ApiResponse<>(true, "Đổi mật khẩu thành công", user);
             }
        }

    @Override
    public ApiResponse<User> requestTrackingUser(RequestTracking requestTracking) {
        return null;
    }

    // Phương thức để lấy danh sách các người dùng đã theo dõi
    private List<String> getFollowedUserIds(ObjectId currentUserId) {
        Criteria criteria = Criteria.where("idFollower").is(currentUserId).and("status").is(1); // Lọc ra các người dùng đã theo dõi
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.project().and("idFollowing").as("idFollowing") // Chọn ra trường idFollowing
        );
        AggregationResults<Follows> results = mongoTemplate.aggregate(aggregation, "follows", Follows.class);
        List<String> followedUserIds = new ArrayList<>();
        for (Follows follows : results.getMappedResults()) {
            followedUserIds.add(String.valueOf(follows.getIdFollowing()));
        }
        return followedUserIds;
    }
    @Override
    public ApiResponse<List<RequestGetAllUserByFollows>> getAllUserByFollows(String userId) {
        ObjectId objectId = new ObjectId(userId);
        // Xác định các người dùng đã theo dõi
        List<String> listUserId = getFollowedUserIds(objectId);
        Query query = new Query().limit(20);
        List<User> listUser = mongoTemplate.find(query, User.class);
        List<RequestGetAllUserByFollows> resultList = listUser.stream()
                .filter(user -> !listUserId.contains(user.getId()) && !userId.contains(user.getId()))
                .map(user -> {
                    return new RequestGetAllUserByFollows(user.getId(),user.getUsername(),user.getAvatarImg(),user.getName());
                })
                .toList();
        return new ApiResponse<List<RequestGetAllUserByFollows>>(true, "Success", resultList);
    }

    @Override
    public ApiResponse<User> getDetailUserById(User req) {
        if (!req.getId().isEmpty()) {
            User resultUser = mongoTemplate.findOne(Query.query(Criteria.where("id").is(req.getId())), User.class, "users");
            return Optional.ofNullable(resultUser)
                    .map(user -> new ApiResponse<User>(true, "Success", user))
                    .orElse(new ApiResponse<>(false, "No data", null));
        }
        return new ApiResponse<>(false, "No data", null);
    }

    @Override
    public ApiResponse<User> updateUser(RequestUpdateUser user) {
        Query query = new Query(Criteria.where("id").is(user.getId()));
        User resultUser = mongoTemplate.findOne(query, User.class, "users");
        if (resultUser == null) {
            return new ApiResponse<>(false, "User not found", null);
        }
        if(!user.getEmail().isEmpty()) resultUser.setEmail(user.getEmail());
        if(!user.getBio().isEmpty()) resultUser.setBio(user.getBio());
        if(!user.getUsername().isEmpty()) resultUser.setUsername(user.getUsername());
        if(!user.getGender().isEmpty()) resultUser.setGender(user.getGender());
        if(!user.getPhoneNumber().isEmpty()) resultUser.setPhoneNumber(user.getPhoneNumber());
        if(!user.getWebsite().isEmpty()) resultUser.setWebsite(user.getWebsite());
        if(!user.getName().isEmpty()) resultUser.setName(user.getName());
        if(!user.getAvatarImg().isEmpty()) resultUser.setAvatarImg(user.getAvatarImg());
        mongoTemplate.save(resultUser,"users");
        return new ApiResponse<>(true, "Update Success!",resultUser);
    }
    public User findUserById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, User.class, "users");
    }


    @Override
    public List<User> findUsersByIds(List<String> ids) {
        Query query = new Query(Criteria.where("_id").in(ids));
        return mongoTemplate.find(query, User.class, "users");
    }

    @Override
    public ApiResponse<GroupChat> createGroupChat(RequestCreateGroupChat requestCreateGroupChat) {
        GroupChat groupChat = new GroupChat();
        groupChat.setGroupName(requestCreateGroupChat.getGroupName());
        groupChat.setMemberIds(requestCreateGroupChat.getMemberIds());
        mongoTemplate.save(groupChat, "groupchats");
        return new ApiResponse<>(true, "Group chat created successfully", groupChat);
    }

    @Override
    public ApiResponse<List<GroupChat>> getGroupChatsByUserId(String userId) {
        Query query = new Query(Criteria.where("memberIds").in(userId));
        List<GroupChat> groupChats = mongoTemplate.find(query, GroupChat.class, "groupchats");
        return new ApiResponse<>(true, "Group chats retrieved successfully", groupChats);
    }

    @Override
    public ApiResponse<GroupChat> addMemberToGroupChat(String groupId, String memberId) {
        Query query = new Query(Criteria.where("_id").is(groupId));
        GroupChat groupChat = mongoTemplate.findOne(query, GroupChat.class, "groupchats");
        if (groupChat != null) {
            List<String> memberIds = groupChat.getMemberIds();
            if (!memberIds.contains(memberId)) {
                memberIds.add(memberId);
                groupChat.setMemberIds(memberIds);
                mongoTemplate.save(groupChat, "groupchats");
                return new ApiResponse<>(true, "Member added to group chat successfully", groupChat);
            } else {
                return new ApiResponse<>(false, "Member already exists in the group chat", null);
            }
        } else {
            return new ApiResponse<>(false, "Group chat not found", null);
        }
    }

    @Override
    public ApiResponse<GroupChat> removeMemberFromGroupChat(String groupId, String memberId) {
        Query query = new Query(Criteria.where("_id").is(groupId));
        GroupChat groupChat = mongoTemplate.findOne(query, GroupChat.class, "groupchats");
        if (groupChat != null) {
            List<String> memberIds = groupChat.getMemberIds();
            if (memberIds.contains(memberId)) {
                memberIds.remove(memberId);
                groupChat.setMemberIds(memberIds);
                mongoTemplate.save(groupChat, "groupchats");
                return new ApiResponse<>(true, "Member removed from group chat successfully", groupChat);
            } else {
                return new ApiResponse<>(false, "Member does not exist in the group chat", null);
            }
        } else {
            return new ApiResponse<>(false, "Group chat not found", null);
        }
    }
}