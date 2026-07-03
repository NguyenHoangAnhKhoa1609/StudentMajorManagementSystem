Công nghệ sử dụng
-Java 24, Spring Boot (Spring Data JPA, Spring Web), Hibernate, MySQL, Maven
 Tạo Database
-Trước khi run,tạo một Database trống trong MySQL: CREATE DATABASE student_management;


Update:
/auth 
/register: dki ,
/login: đăng nhập ,
/me: Thông tin cá nhân , 
/change-password: đổi mk ,
/forgot-password: quên mk

/student
Xem danh sách sv được phân theo page
Tìm kiếm sinh viên theo tên có phân trang
Lọc danh sách sinh viên theo ID major
Báo cáo Xuất dữ liệu CSV
Upload avatar cho User

Phần cấu hình JWT với phân quyền  hơi khó với em, em cũng gặp vài lỗi chặn 403 do lệch pha giữa Token và cấu hình Security,Chỗ nào anh thấy chưa hợp lí và sai sót thì anh nhắn để em cải thiện lại những chỗ đó nha anh
