-- phpMyAdmin SQL Dump
-- version 3.4.5
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jun 08, 2013 at 04:15 AM
-- Server version: 5.5.16
-- PHP Version: 5.3.8

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";
CREATE DATABASE IF NOT EXISTS Future;
use Future;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `Future`
--

-- --------------------------------------------------------

--
-- Table structure for table `Employee`
--

CREATE TABLE IF NOT EXISTS `Employee` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(250) NOT NULL,
  `age` int(11) NOT NULL,
  `employee` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `File_File`
--

CREATE TABLE IF NOT EXISTS `File_File` (
  `idFile` int(11) NOT NULL AUTO_INCREMENT,
  `idUsers` varchar(255) NOT NULL,
  `idGroup` varchar(45) NOT NULL,
  `idApplication` varchar(45) NOT NULL,
  `name` varchar(255) NOT NULL,
  `parentDir` int(11) NOT NULL,
  `realPath` varchar(255) NOT NULL,
  `size` double NOT NULL,
  `type` varchar(20) NOT NULL DEFAULT '0',
  `starred` tinyint(1) NOT NULL,
  `description` text NOT NULL,
  PRIMARY KEY (`idFile`),
  KEY `File_File_name` (`name`),
  KEY `File_File_UserAppGroup` (`idGroup`,`idApplication`,`idUsers`),
  KEY `parentDir` (`parentDir`),
  KEY `type` (`type`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=23 ;

--
-- Dumping data for table `File_File`
--

INSERT INTO `File_File` (`idFile`, `idUsers`, `idGroup`, `idApplication`, `name`, `parentDir`, `realPath`, `size`, `type`, `starred`, `description`) VALUES
(4, 'polla', 'administrator', 'File Manager', 'IPSUR.pdf', 8, '../uploads/File Manager/polla/oc2pudyg9y/', 1380092, 'pdf', 0, ''),
(5, 'polla', 'administrator', 'File Manager', 'MS_Excel.docx', 8, '../uploads/File Manager/polla/84qmot64gt/', 1631991, 'docx', 0, ''),
(6, 'polla', 'administrator', 'File Manager', 'Thesis_4tha.doc', 8, '../uploads/File Manager/polla/3wtnropkw1/', 1997312, 'doc', 0, ''),
(7, 'polla', 'administrator', 'File Manager', 'The_requirements_are.doc', 9, '../uploads/File Manager/polla/hsi4rrb69h/', 19968, 'doc', 0, ''),
(8, 'polla', 'administrator', 'File Manager', 'polla', 9, '', 4000, 'dir', 0, ''),
(9, 'polla', 'administrator', 'File Manager', 'sanar', 0, '', 4000, 'dir', 0, ''),
(10, 'polla', 'administrator', 'File Manager', 'IMG269.jpg', 0, '../uploads/File Manager/polla/8h5y9hjtxn/', 567577, 'jpg', 0, ''),
(11, 'polla', 'administrator', 'File Manager', '23092010838.jpg', 0, '../uploads/File Manager/polla/kfqzfa3ij8/', 732460, 'jpg', 0, ''),
(12, 'polla', 'administrator', 'File Manager', '15022010720.jpg', 0, '../uploads/File Manager/polla/pq4kui1f4w/', 707885, 'jpg', 0, ''),
(13, 'polla', 'administrator', 'File Manager', '09032010721.jpg', 0, '../uploads/File Manager/polla/ubffihnuvn/', 1035425, 'jpg', 0, ''),
(14, 'polla', 'administrator', 'File Manager', '28032010744.jpg', 0, '../uploads/File Manager/polla/3ozxn1w7ar/', 969454, 'jpg', 0, ''),
(20, 'tano', 'administrator', 'File Manager', '15022010720.jpg', 0, '../uploads/File Manager/tano/prdkqh8zik/', 707885, 'jpg', 1, 'polla is here'),
(21, 'tano', 'administrator', 'File Manager', '26032010741.jpg', 0, '../uploads/File Manager/tano/wnvx8boxy9/', 699566, 'jpg', 1, ''),
(22, 'tano', 'administrator', 'File Manager', '23092010838.jpg', 0, '../uploads/File Manager/tano/5yeh4jgnqj/', 732460, 'jpg', 0, '');

-- --------------------------------------------------------

--
-- Table structure for table `File_Lable`
--

CREATE TABLE IF NOT EXISTS `File_Lable` (
  `idLable` varchar(255) NOT NULL,
  `idFile` int(11) NOT NULL,
  `idGroup` varchar(45) NOT NULL,
  `idApplication` varchar(45) NOT NULL,
  `idUsers` varchar(255) NOT NULL,
  PRIMARY KEY (`idLable`,`idFile`,`idGroup`,`idApplication`,`idUsers`),
  KEY `fk_Lable_File` (`idFile`),
  KEY `fk_Lable_AppGroup` (`idGroup`,`idApplication`,`idUsers`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `File_Lable`
--

INSERT INTO `File_Lable` (`idLable`, `idFile`, `idGroup`, `idApplication`, `idUsers`) VALUES
('DaDa', 7, 'administrator', 'File Manager', 'polla'),
('pollla', 7, 'administrator', 'File Manager', 'polla'),
('SOSO', 7, 'administrator', 'File Manager', 'polla'),
('DaDa', 8, 'administrator', 'File Manager', 'polla'),
('SOSO', 8, 'administrator', 'File Manager', 'polla'),
('111', 20, 'administrator', 'File Manager', 'tano'),
('222', 20, 'administrator', 'File Manager', 'tano'),
('rrrrr', 20, 'administrator', 'File Manager', 'tano'),
('xxxxx', 20, 'administrator', 'File Manager', 'tano'),
('222', 21, 'administrator', 'File Manager', 'tano'),
('rrrrr', 21, 'administrator', 'File Manager', 'tano'),
('xxxxx', 21, 'administrator', 'File Manager', 'tano');

-- --------------------------------------------------------

--
-- Table structure for table `File_Sharing`
--

CREATE TABLE IF NOT EXISTS `File_Sharing` (
  `owner` varchar(255) NOT NULL DEFAULT '',
  `sharedWith` varchar(255) NOT NULL,
  `idGroup` varchar(45) NOT NULL,
  `idApplication` varchar(45) NOT NULL,
  `idFile` int(11) NOT NULL,
  PRIMARY KEY (`sharedWith`,`idGroup`,`idApplication`,`idFile`,`owner`),
  KEY `File_FileSharing_idFile` (`idFile`),
  KEY `File_FileSharing_UsersAppGroup` (`idGroup`,`idApplication`,`sharedWith`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `File_Sharing`
--

INSERT INTO `File_Sharing` (`owner`, `sharedWith`, `idGroup`, `idApplication`, `idFile`) VALUES
('polla', 'babo', 'administrator', 'File Manager', 9),
('polla', 'babo', 'administrator', 'File Manager', 10),
('polla', 'tano', 'administrator', 'File Manager', 10);

-- --------------------------------------------------------

--
-- Table structure for table `General_Application`
--

CREATE TABLE IF NOT EXISTS `General_Application` (
  `idApplication` varchar(45) NOT NULL,
  `storageMode` set('unshareable','shareable','shared','owners') NOT NULL DEFAULT 'unshareable',
  PRIMARY KEY (`idApplication`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `General_Application`
--

INSERT INTO `General_Application` (`idApplication`, `storageMode`) VALUES
('Calculator', 'unshareable'),
('File Manager', 'owners');

-- --------------------------------------------------------

--
-- Table structure for table `General_Group`
--

CREATE TABLE IF NOT EXISTS `General_Group` (
  `idGroup` varchar(45) NOT NULL,
  `storageSizeLimit` bigint(20) DEFAULT '0',
  `maxFileLimit` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`idGroup`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `General_Group`
--

INSERT INTO `General_Group` (`idGroup`, `storageSizeLimit`, `maxFileLimit`) VALUES
('administrator', 99999999999999999, 999999999999),
('user', 200000, 20000);

-- --------------------------------------------------------

--
-- Table structure for table `General_Users`
--

CREATE TABLE IF NOT EXISTS `General_Users` (
  `idUsers` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `dateOfBirth` date NOT NULL,
  PRIMARY KEY (`idUsers`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `General_Users`
--

INSERT INTO `General_Users` (`idUsers`, `password`, `name`, `dateOfBirth`) VALUES
('babo', 'babo', 'babo', '2013-01-02'),
('elaf', 'eaf', 'Elaf', '2012-12-17'),
('memo', 'memo', 'memo', '2013-01-01'),
('polla', 'sanar', 'Polla A. Fattah', '1981-10-05'),
('tano', 'tano', 'Tano', '2012-12-04');

-- --------------------------------------------------------

--
-- Table structure for table `General_UsersAppGroup`
--

CREATE TABLE IF NOT EXISTS `General_UsersAppGroup` (
  `idGroup` varchar(45) NOT NULL,
  `idApplication` varchar(45) NOT NULL,
  `idUsers` varchar(255) NOT NULL,
  `usedStorageSize` double DEFAULT '0',
  PRIMARY KEY (`idGroup`,`idApplication`,`idUsers`),
  KEY `General_UsersAppGroup_idApplication` (`idApplication`),
  KEY `General_UsersAppGroup_idGroup` (`idGroup`),
  KEY `General_UsersAppGroup_idUsers` (`idUsers`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `General_UsersAppGroup`
--

INSERT INTO `General_UsersAppGroup` (`idGroup`, `idApplication`, `idUsers`, `usedStorageSize`) VALUES
('administrator', 'File Manager', 'babo', 1293874213984),
('administrator', 'File Manager', 'polla', 38904300),
('administrator', 'File Manager', 'tano', 5940523),
('user', 'File Manager', 'memo', 431242342435);

-- --------------------------------------------------------

--
-- Table structure for table `General_Versioninig`
--

CREATE TABLE IF NOT EXISTS `General_Versioninig` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `table` varchar(255) NOT NULL,
  `version` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `Student`
--

CREATE TABLE IF NOT EXISTS `Student` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `stage` int(11) NOT NULL,
  `gender` varchar(1) NOT NULL,
  `dates` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `File_File`
--
ALTER TABLE `File_File`
  ADD CONSTRAINT `File_File_fk_UserAppGroup` FOREIGN KEY (`idGroup`, `idApplication`, `idUsers`) REFERENCES `General_UsersAppGroup` (`idGroup`, `idApplication`, `idUsers`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `File_Lable`
--
ALTER TABLE `File_Lable`
  ADD CONSTRAINT `fk_Lable_AppGroup` FOREIGN KEY (`idGroup`, `idApplication`, `idUsers`) REFERENCES `General_UsersAppGroup` (`idGroup`, `idApplication`, `idUsers`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_Lable_File` FOREIGN KEY (`idFile`) REFERENCES `File_File` (`idFile`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `File_Sharing`
--
ALTER TABLE `File_Sharing`
  ADD CONSTRAINT `File_FileSharing_fk_idFile` FOREIGN KEY (`idFile`) REFERENCES `File_File` (`idFile`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `File_FileSharing_fk_UsersAppGroup` FOREIGN KEY (`idGroup`, `idApplication`, `sharedWith`) REFERENCES `General_UsersAppGroup` (`idGroup`, `idApplication`, `idUsers`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `General_UsersAppGroup`
--
ALTER TABLE `General_UsersAppGroup`
  ADD CONSTRAINT `General_UsersAppGroup_fk_idApplication` FOREIGN KEY (`idApplication`) REFERENCES `General_Application` (`idApplication`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `General_UsersAppGroup_fk_idGroup` FOREIGN KEY (`idGroup`) REFERENCES `General_Group` (`idGroup`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `General_UsersAppGroup_fk_idUsers` FOREIGN KEY (`idUsers`) REFERENCES `General_Users` (`idUsers`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
