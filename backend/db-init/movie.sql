-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 23, 2026 at 04:04 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `movie_management`
--

-- --------------------------------------------------------

--
-- Table structure for table `bookings`
--

CREATE TABLE `bookings` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `showtime_id` bigint(20) NOT NULL,
  `customer_name` varchar(255) DEFAULT NULL,
  `customer_email` varchar(255) DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'CONFIRMED',
  `payment_method` varchar(20) DEFAULT NULL,
  `payment_txn_id` varchar(100) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `paid_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `bookings`
--

INSERT INTO `bookings` (`id`, `user_id`, `showtime_id`, `customer_name`, `customer_email`, `quantity`, `unit_price`, `total_price`, `status`, `payment_method`, `payment_txn_id`, `created_at`, `paid_at`) VALUES
(6, 8, 44, NULL, NULL, 3, 85000.00, 255000.00, 'CONFIRMED', NULL, NULL, '2025-10-19 07:16:54', NULL),
(7, 8, 43, NULL, NULL, 10, 85000.00, 850000.00, 'CONFIRMED', NULL, NULL, '2025-10-19 07:17:43', NULL),
(8, 8, 44, NULL, NULL, 1, 85000.00, 85000.00, 'CONFIRMED', NULL, NULL, '2025-10-19 07:18:09', NULL),
(9, 11, 44, 'trung', 'trungda555@pewpew.com', 2, 85000.00, 170000.00, 'CONFIRMED', NULL, NULL, '2025-10-19 08:02:05', NULL),
(10, 8, 45, 'Đặng Nam Anh', 'namanh@gmail.com', 3, 55000.00, 165000.00, 'CONFIRMED', NULL, NULL, '2025-10-19 08:03:47', NULL),
(11, 7, 45, 'admin', 'namanhdang0905@gmail.com', 2, 55000.00, 99000.00, 'PAID', 'VNPAY', '1760929191860', '2025-10-19 19:58:56', '2025-10-19 19:59:51'),
(12, 8, 46, 'Đặng Nam Anh', 'namanh@gmail.com', 3, 55000.00, 148500.00, 'PENDING', 'VNPAY', 'BK12-1760929674235', '2025-10-19 20:07:54', NULL),
(13, 8, 46, 'Đặng Nam Anh', 'namanh@gmail.com', 3, 55000.00, 148500.00, 'PENDING', 'VNPAY', 'BK13-1760930082627', '2025-10-19 20:14:42', NULL),
(14, 8, 46, 'Đặng Nam Anh', 'namanh@gmail.com', 2, 55000.00, 110000.00, 'PAID', 'VNPAY', '15210399', '2025-10-19 20:23:16', '2025-10-19 20:23:39'),
(15, 8, 47, 'Đặng Nam Anh', 'namanh@gmail.com', 1, 60000.00, 10000.00, 'PAID', 'VNPAY', '15210492', '2025-10-19 20:59:06', '2025-10-19 20:59:32'),
(16, 11, 46, 'trung', 'trungda555@pewpew.com', 5, 55000.00, 247500.00, 'PAID', 'VNPAY', '15210501', '2025-10-19 21:05:29', '2025-10-19 21:05:52'),
(17, 11, 47, 'trung', 'trungda555@pewpew.com', 5, 60000.00, 300000.00, 'PAID', 'VNPAY', '15210542', '2025-10-19 21:22:44', '2025-10-19 21:23:03'),
(18, 11, 47, 'trung', 'trungda555@pewpew.com', 6, 60000.00, 360000.00, 'FAILED', 'VNPAY', '15210557', '2025-10-19 21:30:40', NULL),
(19, 8, 47, 'Đặng Nam Anh', 'namanh@gmail.com', 4, 60000.00, 240000.00, 'PAID', 'VNPAY', '15210560', '2025-10-19 21:32:17', '2025-10-19 21:32:39'),
(20, 8, 46, 'Đặng Nam Anh', 'namanh@gmail.com', 3, 55000.00, 165000.00, 'PAID', 'VNPAY', '15210598', '2025-10-19 22:08:51', '2025-10-19 22:09:15'),
(21, 8, 43, 'Khách A', 'a@example.com', 2, 85000.00, 170000.00, 'CONFIRMED', NULL, NULL, '2025-10-10 02:12:00', NULL),
(22, 7, 45, 'Khách B', 'b@example.com', 1, 55000.00, 55000.00, 'PAID', 'VNPAY', '15100101', '2025-10-10 03:20:00', '2025-10-10 03:22:30'),
(23, 11, 46, 'Khách C', 'c@example.com', 3, 55000.00, 165000.00, 'PAID', 'VNPAY', '15110102', '2025-10-11 07:05:00', '2025-10-11 07:06:10'),
(24, 8, 43, 'Khách D', 'd@example.com', 2, 85000.00, 170000.00, 'CONFIRMED', NULL, NULL, '2025-10-12 01:30:00', NULL),
(25, 9, 46, 'Khách E', 'e@example.com', 1, 55000.00, 55000.00, 'PENDING', 'VNPAY', '15120103', '2025-10-12 04:45:00', NULL),
(26, 11, 47, 'Khách F', 'f@example.com', 4, 60000.00, 240000.00, 'PAID', 'VNPAY', '15130104', '2025-10-13 09:10:00', '2025-10-13 09:11:05'),
(27, 7, 45, 'Khách G', 'g@example.com', 3, 55000.00, 165000.00, 'PENDING', 'VNPAY', '15140105', '2025-10-14 05:20:00', NULL),
(28, 8, 44, 'Khách H', 'h@example.com', 1, 85000.00, 85000.00, 'CONFIRMED', NULL, NULL, '2025-10-15 02:00:00', NULL),
(29, 11, 47, 'Khách I', 'i@example.com', 5, 60000.00, 300000.00, 'PAID', 'VNPAY', '15160106', '2025-10-16 11:40:00', '2025-10-16 11:41:12'),
(30, 10, 46, 'Khách J', 'j@example.com', 2, 55000.00, 110000.00, 'PAID', 'VNPAY', '15170107', '2025-10-17 08:25:00', '2025-10-17 08:26:10'),
(31, 8, 45, 'Khách K', 'k@example.com', 2, 55000.00, 110000.00, 'PAID', 'VNPAY', '15180108', '2025-10-18 03:05:00', '2025-10-18 03:06:00'),
(32, 9, 43, 'Khách L', 'l@example.com', 3, 85000.00, 255000.00, 'CONFIRMED', NULL, NULL, '2025-10-18 06:30:00', NULL),
(33, 8, 43, 'Khách A', 'a@example.com', 2, 85000.00, 170000.00, 'CONFIRMED', NULL, NULL, '2025-09-10 02:12:00', NULL),
(34, 7, 45, 'Khách B', 'b@example.com', 1, 55000.00, 55000.00, 'PAID', 'VNPAY', '15090101', '2025-09-10 03:20:00', '2025-09-10 03:22:30'),
(35, 11, 46, 'Khách C', 'c@example.com', 3, 55000.00, 165000.00, 'PAID', 'VNPAY', '15091102', '2025-09-11 07:05:00', '2025-09-11 07:06:10'),
(36, 8, 43, 'Khách D', 'd@example.com', 2, 85000.00, 170000.00, 'CONFIRMED', NULL, NULL, '2025-09-12 01:30:00', NULL),
(37, 9, 46, 'Khách E', 'e@example.com', 1, 55000.00, 55000.00, 'PENDING', 'VNPAY', '15092103', '2025-09-12 04:45:00', NULL),
(38, 11, 47, 'Khách F', 'f@example.com', 4, 60000.00, 240000.00, 'PAID', 'VNPAY', '15093104', '2025-09-13 09:10:00', '2025-09-13 09:11:05'),
(39, 7, 45, 'Khách G', 'g@example.com', 3, 55000.00, 165000.00, 'PENDING', 'VNPAY', '15094105', '2025-09-14 05:20:00', NULL),
(40, 8, 44, 'Khách H', 'h@example.com', 1, 85000.00, 85000.00, 'CONFIRMED', NULL, NULL, '2025-09-15 02:00:00', NULL),
(41, 11, 47, 'Khách I', 'i@example.com', 5, 60000.00, 300000.00, 'PAID', 'VNPAY', '15096106', '2025-09-16 11:40:00', '2025-09-16 11:41:12'),
(42, 10, 46, 'Khách J', 'j@example.com', 2, 55000.00, 110000.00, 'PAID', 'VNPAY', '15097107', '2025-09-17 08:25:00', '2025-09-17 08:26:10'),
(43, 8, 45, 'Khách K', 'k@example.com', 2, 55000.00, 110000.00, 'PAID', 'VNPAY', '15098108', '2025-09-18 03:05:00', '2025-09-18 03:06:00'),
(44, 9, 43, 'Khách L', 'l@example.com', 3, 85000.00, 255000.00, 'CONFIRMED', NULL, NULL, '2025-09-18 06:30:00', NULL),
(45, 8, 56, 'Đặng Nam Anh', 'namanh@gmail.com', 10, 85000.00, 765000.00, 'PAID', 'VNPAY', '15211090', '2025-10-20 04:09:43', '2025-10-20 04:10:07'),
(46, 8, 72, 'Đặng Nam Anh', 'namanh@gmail.com', 5, 55000.00, 247500.00, 'PAID', 'VNPAY', '15211640', '2025-10-20 19:06:54', '2025-10-20 19:07:40'),
(47, 8, 79, 'Đặng Nam Anh', 'namanh@gmail.com', 1, 80000.00, 80000.00, 'PENDING', 'VNPAY', 'BK47-1761367077295', '2025-10-24 21:37:57', NULL),
(48, 8, 79, 'Đặng Nam Anh', 'namanh@gmail.com', 1, 80000.00, 80000.00, 'PENDING', 'VNPAY', 'BK48-1761404383762', '2025-10-25 07:59:43', NULL),
(49, 7, 80, 'admin', 'namanhdang0905@gmail.com', 1, 85000.00, 85000.00, 'PENDING', 'VNPAY', 'BK49-1761452095200', '2025-10-25 21:14:55', NULL),
(50, 7, 80, 'admin', 'namanhdang0905@gmail.com', 1, 85000.00, 85000.00, 'PENDING', 'VNPAY', 'BK50-1761453410422', '2025-10-25 21:36:50', NULL),
(51, 8, 81, 'Đặng Nam Anh', 'namanh@gmail.com', 1, 50000.00, 45000.00, 'PENDING', 'VNPAY', 'BK51-1762510823988', '2025-11-07 03:20:23', NULL),
(52, 8, 81, 'Đặng Nam Anh', 'namanh@gmail.com', 1, 50000.00, 50000.00, 'PENDING', 'VNPAY', 'BK52-1762510939725', '2025-11-07 03:22:19', NULL),
(53, 8, 81, 'Đặng Nam Anh', 'namanh@gmail.com', 2, 50000.00, 100000.00, 'PAID', 'VNPAY', '15245337', '2025-11-07 03:27:53', '2025-11-07 03:28:44'),
(54, 8, 81, 'Đặng Nam Anh', 'namanh@gmail.com', 3, 50000.00, 150000.00, 'PAID', 'VNPAY', '15245478', '2025-11-07 04:52:15', '2025-11-07 04:52:38');

-- --------------------------------------------------------

--
-- Table structure for table `cinemas`
--

CREATE TABLE `cinemas` (
  `id` bigint(20) NOT NULL,
  `status` varchar(20) NOT NULL,
  `name` varchar(150) NOT NULL,
  `address` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `cinemas`
--

INSERT INTO `cinemas` (`id`, `status`, `name`, `address`) VALUES
(13, 'ACTIVE', '4SCinema Đà Nẵng', '45 Bạch Đằng, Đà Nẵng'),
(15, 'ACTIVE', '4SCinema Long Biên', 'Thạch Bàn, Long Biên, Hà Nội'),
(24, 'ACTIVE', '4SCinema Cầu Giấy', 'Cầu Giấy, Hà Nội'),
(25, 'ACTIVE', '4SCinema Hà Đông', 'Hà Đông, Hà Nội');

-- --------------------------------------------------------

--
-- Table structure for table `genres`
--

CREATE TABLE `genres` (
  `id` bigint(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `slug` varchar(120) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `genres`
--

INSERT INTO `genres` (`id`, `name`, `slug`, `created_at`, `updated_at`) VALUES
(21, 'Kinh dị', 'kinh-di', '2025-09-27 21:15:52', NULL),
(22, 'Khoa học viễn tưởng', 'khoa-hoc-vien-tuong', '2025-09-27 21:16:09', NULL),
(25, 'Hoạt hình', 'hoat-hinh', '2025-09-28 06:19:36', NULL),
(26, 'Hành động', 'hanh-dong', '2025-09-29 03:59:56', NULL),
(27, 'Tình cảm', 'tinh-cam', '2025-09-29 03:59:56', NULL),
(28, 'Hài', 'hai', '2025-09-29 03:59:56', NULL),
(29, 'Tài liệu', 'tai-lieu', '2025-09-29 03:59:56', NULL),
(30, 'Âm nhạc', 'am-nhac', '2025-09-29 03:59:56', NULL);

--
-- Triggers `genres`
--
DELIMITER $$
CREATE TRIGGER `trg_genres_set_updated_at` BEFORE UPDATE ON `genres` FOR EACH ROW BEGIN
  SET NEW.updated_at = CURRENT_TIMESTAMP;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `movies`
--

CREATE TABLE `movies` (
  `id` bigint(20) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `duration` int(10) UNSIGNED DEFAULT NULL,
  `release_date` date DEFAULT NULL,
  `language` varchar(64) DEFAULT NULL,
  `country` varchar(64) DEFAULT NULL,
  `status` varchar(32) NOT NULL,
  `age_rating` varchar(32) DEFAULT NULL,
  `primary_genre_id` bigint(20) DEFAULT NULL,
  `poster_url` varchar(512) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT NULL,
  `year` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `movies`
--

INSERT INTO `movies` (`id`, `title`, `description`, `duration`, `release_date`, `language`, `country`, `status`, `age_rating`, `primary_genre_id`, `poster_url`, `created_at`, `updated_at`, `year`) VALUES
(31, 'Your Name', 'Hai thiếu niên ở hai vùng khác nhau bỗng nhiên hoán đổi thân xác, mở ra hành trình tìm kiếm và kết nối vượt thời gian, không gian.', 123, '2025-10-31', 'Vietsub', 'Nhật Bản', 'COMING_SOON', '13', NULL, '/uploads/1759040301529_YourName_poster.jpg', '2025-09-28 06:18:57', '2025-10-20 10:40:45', NULL),
(32, 'Train To Busan', 'Một trận đại dịch zombie bùng phát trên chuyến tàu cao tốc từ Seoul đến Busan, buộc hành khách phải đấu tranh sinh tồn trong tuyệt vọng.', 145, '2025-09-28', 'Vietsub', 'Hàn Quốc', 'RELEASED', '17', NULL, '/uploads/1759040967275_TrainToBusan_poster.jpg', '2025-09-28 06:29:54', NULL, NULL),
(34, 'Dune 2', 'Paul Atreides tiếp tục hành trình trả thù cho gia đình, nắm lấy định mệnh của mình và dẫn dắt người Fremen chống lại đế chế Harkonnen.', 166, '2025-09-01', 'Vietsub', 'Mỹ', 'RELEASED', '13', NULL, '/uploads/1759119000466_Dune-Movie-Main-Poster.jpg', '2025-09-29 11:09:30', '2025-10-20 10:48:41', NULL),
(35, 'Avengers: End Game', 'Sau khi Thanos xóa sổ nửa vũ trụ, các Avengers còn sống sót hợp sức trong trận chiến cuối cùng để đảo ngược thảm kịch.', 181, '2025-08-26', 'Vietsub', 'Mỹ', 'RELEASED', '13', NULL, '/uploads/1759119028663_Avengers_EndGame_poster.jpg', '2025-09-29 11:09:30', '2025-10-20 10:49:02', NULL),
(36, 'Avatar: The Way of Water', 'Jake Sully và gia đình phải rời bỏ ngôi nhà của mình để khám phá vùng biển Pandora, đối mặt với kẻ thù cũ trong một cuộc chiến sinh tồn mới.', 192, '2025-05-09', 'Vietsub', 'Mỹ', 'RELEASED', '13', NULL, '/uploads/1759119034240_Avatar_poster.jpg', '2025-09-29 11:09:30', '2025-10-20 10:49:13', NULL),
(37, 'Interstella', 'Trong tương lai khi Trái Đất cạn kiệt tài nguyên, một nhóm phi hành gia du hành qua hố sâu vũ trụ để tìm kiếm hành tinh có thể sinh sống.', 169, '2025-12-15', 'Vietsub', 'Mỹ', 'COMING_SOON', '13', NULL, '/uploads/1759119075123_Interstella_poster.jpg', '2025-09-29 11:09:30', '2025-09-29 04:21:23', NULL),
(38, 'Moana', 'Cô gái trẻ Moana dũng cảm vượt đại dương để tìm kiếm vị thần Maui và khôi phục sự cân bằng cho hòn đảo của mình.', 107, '2025-11-05', 'Vietsub', 'Mỹ', 'COMING_SOON', '7', NULL, '/uploads/1759119091093_Moana_poster.jpg', '2025-09-29 11:09:30', '2025-10-20 10:41:33', NULL),
(41, 'Transformer: Age of Extinction', 'Sau trận chiến tàn khốc, loài người quay lưng lại với các Autobot, nhưng một mối đe dọa mới buộc họ phải hợp tác một lần nữa để cứu Trái Đất.', 165, '2026-06-18', 'Vietsub', 'Mỹ', 'COMING_SOON', '13', NULL, '/uploads/1759119238300_Transformer_poster.jpg', '2025-09-29 11:09:30', '2025-09-29 04:22:42', NULL),
(42, 'Venom: Let There Be Carnage', 'Eddie Brock tiếp tục chung sống cùng sinh vật ký sinh Venom, đối đầu với kẻ thù mới — Carnage, một sinh vật ngoài hành tinh tàn bạo.', 97, '2025-05-13', 'Vietsub', 'Mỹ', 'RELEASED', '16', NULL, '/uploads/1759119206948_Venom_poster.jpg', '2025-09-29 11:09:30', '2025-10-20 10:49:28', NULL),
(43, 'Get Out', 'Một chàng trai da màu đến thăm gia đình bạn gái da trắng và phát hiện ra bí mật kinh hoàng ẩn sau vẻ ngoài thân thiện của họ.', 104, '2025-08-08', 'Vietsub', 'Mỹ', 'RELEASED', '17', NULL, '/uploads/1759119193516_get-out-poster.jpg', '2025-09-29 11:09:30', '2025-10-20 10:49:43', NULL),
(44, 'Inside Out 2', 'Riley bước vào tuổi thiếu niên, những cảm xúc mới xuất hiện khiến thế giới nội tâm của cô trở nên hỗn loạn và đầy bất ngờ.', 96, '2025-10-14', 'Vietsub', 'Mỹ', 'RELEASED', '7', NULL, '/uploads/1759119178983_inside-out-2-poster.webp', '2025-09-29 11:09:30', '2025-10-20 10:50:00', NULL),
(45, 'Anabelle', 'Một con búp bê bị ám trở thành cánh cổng cho thế lực tà ác xâm nhập, gieo rắc nỗi kinh hoàng cho một gia đình trẻ.', 99, '2025-10-05', 'Vietsub', 'Mỹ', 'RELEASED', '17', NULL, '/uploads/1759119167388_Annabelle_poster.jpg', '2025-09-29 11:09:30', '2025-10-20 10:50:15', NULL),
(47, 'Bố Già', 'Câu chuyện cảm động về tình cha con trong một khu xóm nhỏ Sài Gòn, nơi người cha lam lũ cố gắng nuôi dạy con giữa cuộc sống bon chen.', 134, '2025-10-25', 'Phụ đề', 'Việt Nam', 'COMING_SOON', '13', NULL, '/uploads/1759157270330_BoGia_poster.jpg', '2025-09-29 14:48:28', '2025-10-20 10:41:57', NULL),
(50, 'Logan', 'Ở tương lai gần, Wolverine già nua phải bảo vệ cô bé dị nhân có sức mạnh giống mình, trong cuộc hành trình cuối cùng đầy cảm xúc.', 132, '2025-10-20', 'Eng', 'Mỹ', 'RELEASED', '13', NULL, '/uploads/1760782943015_Logan_poster.jpg', '2025-10-18 10:23:02', '2025-10-20 10:53:19', NULL),
(51, 'The Platform', 'Một nhà tù thẳng đứng với những tầng lớp khác nhau, nơi thức ăn được phân phát từ trên xuống, phơi bày bản chất thật của con người.', 97, '2025-10-31', 'Eng', 'Tây Ban Nha', 'COMING_SOON', '13', NULL, '/uploads/1760834194344_thePlatform_poster.jpg', '2025-10-19 00:37:18', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `movie_genres`
--

CREATE TABLE `movie_genres` (
  `movie_id` bigint(20) NOT NULL,
  `genre_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `movie_genres`
--

INSERT INTO `movie_genres` (`movie_id`, `genre_id`) VALUES
(31, 25),
(32, 21),
(34, 22),
(34, 26),
(35, 22),
(35, 26),
(36, 22),
(36, 26),
(36, 27),
(37, 22),
(37, 26),
(37, 27),
(38, 25),
(38, 27),
(38, 30),
(41, 22),
(41, 26),
(42, 22),
(42, 26),
(42, 28),
(43, 21),
(44, 25),
(44, 27),
(44, 30),
(45, 21),
(47, 27),
(47, 28),
(47, 30),
(50, 22),
(50, 26),
(50, 27),
(51, 21);

-- --------------------------------------------------------

--
-- Table structure for table `showtimes`
--

CREATE TABLE `showtimes` (
  `id` bigint(20) NOT NULL,
  `movie_id` bigint(20) NOT NULL,
  `cinema_id` bigint(20) NOT NULL,
  `start_time` datetime NOT NULL,
  `price` decimal(10,2) DEFAULT 0.00,
  `end_time` datetime(6) NOT NULL,
  `status` varchar(20) NOT NULL,
  `capacity` int(11) NOT NULL DEFAULT 50,
  `sold_seats` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `showtimes`
--

INSERT INTO `showtimes` (`id`, `movie_id`, `cinema_id`, `start_time`, `price`, `end_time`, `status`, `capacity`, `sold_seats`) VALUES
(43, 34, 13, '2025-10-19 03:54:00', 85000.00, '2025-10-19 06:40:00.000000', 'OPEN', 10, 10),
(44, 34, 13, '2025-10-19 11:52:00', 85000.00, '2025-10-19 14:38:00.000000', 'OPEN', 10, 6),
(45, 35, 15, '2025-10-19 16:30:00', 55000.00, '2025-10-19 19:31:00.000000', 'OPEN', 50, 5),
(46, 36, 13, '2025-10-20 00:00:00', 55000.00, '2025-10-20 03:12:00.000000', 'OPEN', 50, 10),
(47, 44, 15, '2025-10-20 05:00:00', 60000.00, '2025-10-20 06:36:00.000000', 'OPEN', 10, 10),
(48, 34, 13, '2025-10-21 14:45:00', 55000.00, '2025-10-21 17:31:00.000000', 'OPEN', 50, NULL),
(49, 34, 13, '2025-10-21 16:30:00', 55000.00, '2025-10-21 19:16:00.000000', 'OPEN', 50, NULL),
(50, 32, 13, '2025-10-20 05:40:00', 85000.00, '2025-10-20 08:05:00.000000', 'OPEN', 50, NULL),
(51, 32, 13, '2025-10-20 10:45:00', 85000.00, '2025-10-20 13:10:00.000000', 'OPEN', 50, NULL),
(52, 32, 13, '2025-10-20 14:00:00', 85000.00, '2025-10-20 16:25:00.000000', 'OPEN', 50, NULL),
(53, 32, 13, '2025-10-21 10:45:00', 85000.00, '2025-10-21 13:10:00.000000', 'OPEN', 50, NULL),
(54, 32, 13, '2025-10-21 12:30:00', 85000.00, '2025-10-21 14:55:00.000000', 'OPEN', 50, NULL),
(55, 32, 13, '2025-10-21 16:45:00', 85000.00, '2025-10-21 19:10:00.000000', 'OPEN', 50, NULL),
(56, 43, 13, '2025-10-20 13:45:00', 85000.00, '2025-10-20 15:29:00.000000', 'OPEN', 50, 10),
(57, 43, 13, '2025-10-20 16:55:00', 85000.00, '2025-10-20 18:39:00.000000', 'OPEN', 50, NULL),
(58, 36, 13, '2025-10-19 17:45:00', 85000.00, '2025-10-19 20:57:00.000000', 'OPEN', 50, NULL),
(59, 36, 13, '2025-10-20 07:50:00', 85000.00, '2025-10-20 11:02:00.000000', 'OPEN', 50, NULL),
(60, 36, 13, '2025-10-20 12:40:00', 85000.00, '2025-10-20 15:52:00.000000', 'OPEN', 50, NULL),
(61, 36, 13, '2025-10-21 10:45:00', 55000.00, '2025-10-21 13:57:00.000000', 'OPEN', 30, NULL),
(62, 36, 13, '2025-10-21 13:50:00', 55000.00, '2025-10-21 17:02:00.000000', 'OPEN', 30, NULL),
(63, 44, 13, '2025-10-21 03:30:00', 55000.00, '2025-10-21 05:06:00.000000', 'OPEN', 10, NULL),
(64, 44, 13, '2025-10-21 05:45:00', 55000.00, '2025-10-21 07:21:00.000000', 'OPEN', 10, NULL),
(65, 44, 13, '2025-10-21 08:50:00', 55000.00, '2025-10-21 10:26:00.000000', 'OPEN', 10, NULL),
(66, 45, 15, '2025-10-21 11:00:00', 55000.00, '2025-10-21 12:39:00.000000', 'OPEN', 50, NULL),
(67, 45, 15, '2025-10-21 14:30:00', 55000.00, '2025-10-21 16:09:00.000000', 'OPEN', 50, NULL),
(68, 50, 15, '2025-10-21 11:00:00', 60000.00, '2025-10-21 13:12:00.000000', 'OPEN', 10, NULL),
(69, 50, 15, '2025-10-21 13:45:00', 60000.00, '2025-10-21 15:57:00.000000', 'OPEN', 10, NULL),
(70, 35, 13, '2025-10-21 11:00:00', 85000.00, '2025-10-21 14:01:00.000000', 'OPEN', 50, NULL),
(71, 35, 13, '2025-10-21 16:00:00', 85000.00, '2025-10-21 19:01:00.000000', 'OPEN', 50, NULL),
(72, 42, 13, '2025-10-21 12:30:00', 55000.00, '2025-10-21 14:07:00.000000', 'OPEN', 50, 5),
(73, 42, 13, '2025-10-21 15:15:00', 55000.00, '2025-10-21 16:52:00.000000', 'OPEN', 50, NULL),
(74, 34, 15, '2025-10-21 05:30:00', 75000.00, '2025-10-21 08:16:00.000000', 'OPEN', 50, NULL),
(75, 34, 15, '2025-10-21 14:00:00', 75000.00, '2025-10-21 16:46:00.000000', 'OPEN', 50, NULL),
(76, 35, 24, '2025-10-20 18:10:00', 55000.00, '2025-10-20 21:11:00.000000', 'OPEN', 50, NULL),
(77, 35, 24, '2025-10-21 11:15:00', 55000.00, '2025-10-21 14:16:00.000000', 'OPEN', 50, NULL),
(78, 35, 24, '2025-10-21 14:00:00', 55000.00, '2025-10-21 17:01:00.000000', 'OPEN', 50, NULL),
(79, 32, 13, '2025-10-25 04:20:00', 80000.00, '2025-10-25 06:45:00.000000', 'OPEN', 10, NULL),
(80, 32, 13, '2025-10-26 16:30:00', 85000.00, '2025-10-26 18:55:00.000000', 'OPEN', 50, NULL),
(81, 32, 13, '2025-11-07 15:30:00', 50000.00, '2025-11-07 17:55:00.000000', 'OPEN', 50, 5),
(82, 34, 13, '2025-11-09 14:27:00', 85000.00, '2025-11-09 17:13:00.000000', 'OPEN', 50, NULL),
(83, 34, 13, '2025-11-09 14:47:00', 80000.00, '2025-11-09 17:33:00.000000', 'OPEN', 50, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `username` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `status` varchar(30) NOT NULL DEFAULT 'ACTIVE',
  `full_name` varchar(255) DEFAULT NULL,
  `role` varchar(30) NOT NULL DEFAULT 'USER',
  `enabled` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `email` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `status`, `full_name`, `role`, `enabled`, `created_at`, `email`, `password_hash`, `updated_at`) VALUES
(7, 'admin', '$2a$10$kxBt5Dn/soseQXwLWwhbV.iqLkey1XxcZXaG3rNyiAZp5gY0bG3EG', 'ACTIVE', NULL, 'ADMIN', 1, '2025-09-29 11:18:54', 'namanhdang0905@gmail.com', '$2a$10$kxBt5Dn/soseQXwLWwhbV.iqLkey1XxcZXaG3rNyiAZp5gY0bG3EG', '2025-09-29 11:18:54.000000'),
(8, 'namanh1', '$2a$10$3rY5L6rN3k2/qPB2aTjqD.PUEFtzmQftEXgUdwF0rlxJCkZV0FAme', 'ACTIVE', 'Đặng Nam Anh', 'USER', 1, '2025-09-29 11:23:43', 'namanh@gmail.com', '$2a$10$3rY5L6rN3k2/qPB2aTjqD.PUEFtzmQftEXgUdwF0rlxJCkZV0FAme', '2025-09-30 07:20:13.000000'),
(9, 'admin11111', '$2a$10$Nbdsmoc.SOWnxHJR6hDHyuDIIwcUEYQ5LDehM/q9qe61FeVYrLst.', 'ACTIVE', 'Test User', 'USER', 1, '2025-09-30 04:39:18', 'admin@gmail.com', '$2a$10$Nbdsmoc.SOWnxHJR6hDHyuDIIwcUEYQ5LDehM/q9qe61FeVYrLst.', '2025-09-30 04:39:18.000000'),
(10, 'admin1234', '$2a$10$OwUj/Ey83zhXjcNNhkrE3OIfP6dFalRfWPX0jUEtvpnP3iYUAA6pS', 'ACTIVE', 'Test User', 'USER', 1, '2025-09-30 05:33:14', 'adminnn@gmail.com', '$2a$10$OwUj/Ey83zhXjcNNhkrE3OIfP6dFalRfWPX0jUEtvpnP3iYUAA6pS', '2025-09-30 05:33:14.000000'),
(11, 'trung', '$2a$10$WEDHhnaCZ7zRoCH7UKV14./DUSC0urvy.AzJPJCDakPqMlJIcBj2i', 'ACTIVE', NULL, 'USER', 1, '2025-10-19 12:24:03', 'trungda555@pewpew.com', '$2a$10$WEDHhnaCZ7zRoCH7UKV14./DUSC0urvy.AzJPJCDakPqMlJIcBj2i', '2025-10-19 12:24:03.000000');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bookings`
--
ALTER TABLE `bookings`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_bookings_showtime` (`showtime_id`),
  ADD KEY `idx_bookings_user` (`user_id`),
  ADD KEY `idx_bookings_created_at` (`created_at`),
  ADD KEY `idx_bookings_status` (`status`);

--
-- Indexes for table `cinemas`
--
ALTER TABLE `cinemas`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_cinemas_name` (`name`);

--
-- Indexes for table `genres`
--
ALTER TABLE `genres`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_genres_name` (`name`),
  ADD UNIQUE KEY `ux_genres_slug` (`slug`),
  ADD UNIQUE KEY `uq_genres_slug` (`slug`);

--
-- Indexes for table `movies`
--
ALTER TABLE `movies`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_movies_status` (`status`),
  ADD KEY `idx_movies_title` (`title`),
  ADD KEY `idx_movies_created_at` (`created_at`),
  ADD KEY `idx_movies_release_date` (`release_date`),
  ADD KEY `idx_movies_age_rating` (`age_rating`),
  ADD KEY `idx_movies_primary_genre` (`primary_genre_id`);

--
-- Indexes for table `movie_genres`
--
ALTER TABLE `movie_genres`
  ADD PRIMARY KEY (`movie_id`,`genre_id`),
  ADD KEY `fk_mg_genre` (`genre_id`);

--
-- Indexes for table `showtimes`
--
ALTER TABLE `showtimes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_showtimes_movie` (`movie_id`),
  ADD KEY `idx_showtimes_cinema` (`cinema_id`),
  ADD KEY `idx_showtimes_start` (`start_time`),
  ADD KEY `idx_st_movie_time` (`movie_id`,`start_time`),
  ADD KEY `idx_st_cinema_time` (`cinema_id`,`start_time`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `uk_users_email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `bookings`
--
ALTER TABLE `bookings`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=55;

--
-- AUTO_INCREMENT for table `cinemas`
--
ALTER TABLE `cinemas`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT for table `genres`
--
ALTER TABLE `genres`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT for table `movies`
--
ALTER TABLE `movies`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=52;

--
-- AUTO_INCREMENT for table `showtimes`
--
ALTER TABLE `showtimes`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=84;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `bookings`
--
ALTER TABLE `bookings`
  ADD CONSTRAINT `fk_bookings_showtime` FOREIGN KEY (`showtime_id`) REFERENCES `showtimes` (`id`),
  ADD CONSTRAINT `fk_bookings_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `movies`
--
ALTER TABLE `movies`
  ADD CONSTRAINT `fk_movies_primary_genre` FOREIGN KEY (`primary_genre_id`) REFERENCES `genres` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `movie_genres`
--
ALTER TABLE `movie_genres`
  ADD CONSTRAINT `fk_mg_genre` FOREIGN KEY (`genre_id`) REFERENCES `genres` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_mg_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `showtimes`
--
ALTER TABLE `showtimes`
  ADD CONSTRAINT `fk_show_cinema` FOREIGN KEY (`cinema_id`) REFERENCES `cinemas` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_show_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
