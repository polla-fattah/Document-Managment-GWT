-- phpMyAdmin SQL Dump
-- version 3.4.5
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jun 08, 2013 at 04:16 AM
-- Server version: 5.5.16
-- PHP Version: 5.3.8

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `hr`
--

-- --------------------------------------------------------

--
-- Table structure for table `cirtificate`
--

CREATE TABLE IF NOT EXISTS `cirtificate` (
  `idcirtificate` int(11) NOT NULL AUTO_INCREMENT,
  `certificateName` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`idcirtificate`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=10 ;

--
-- Dumping data for table `cirtificate`
--

INSERT INTO `cirtificate` (`idcirtificate`, `certificateName`) VALUES
(5, 'Adola'),
(7, 'sdf'),
(8, 'Deploma'),
(9, 'Higher Deploam');

-- --------------------------------------------------------

--
-- Table structure for table `department`
--

CREATE TABLE IF NOT EXISTS `department` (
  `idDepartment` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`idDepartment`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=7 ;

--
-- Dumping data for table `department`
--

INSERT INTO `department` (`idDepartment`, `name`) VALUES
(1, 'Electrical'),
(2, 'Software'),
(3, 'Civil'),
(4, 'Mechanic'),
(5, 'Water Suply'),
(6, 'Irrigation');

-- --------------------------------------------------------

--
-- Table structure for table `employee`
--

CREATE TABLE IF NOT EXISTS `employee` (
  `idEmployee` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `birthDate` varchar(20) DEFAULT NULL,
  `placeOfBirth` varchar(255) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `bloodGroup` varchar(5) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `mobile` varchar(25) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `lastCirtificate` int(11) DEFAULT NULL,
  `jobTile` int(11) NOT NULL,
  `department` int(11) NOT NULL,
  `picture` int(11) DEFAULT NULL,
  `speciality` varchar(255) DEFAULT NULL,
  `degree` varchar(45) DEFAULT NULL,
  `stage` varchar(45) DEFAULT NULL,
  `riseDate` varchar(20) DEFAULT NULL,
  `increaseDate` varchar(20) DEFAULT NULL,
  `startDate` varchar(20) DEFAULT NULL,
  `baseSalary` int(11) DEFAULT NULL,
  PRIMARY KEY (`idEmployee`),
  KEY `name` (`name`),
  KEY `fk_Employee_cirtificate` (`lastCirtificate`),
  KEY `fk_Employee_JobTile1` (`jobTile`),
  KEY `fk_Employee_Department1` (`department`),
  KEY `fk_Employee_File1` (`picture`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=7 ;

--
-- Dumping data for table `employee`
--

INSERT INTO `employee` (`idEmployee`, `name`, `birthDate`, `placeOfBirth`, `gender`, `bloodGroup`, `address`, `mobile`, `email`, `lastCirtificate`, `jobTile`, `department`, `picture`, `speciality`, `degree`, `stage`, `riseDate`, `increaseDate`, `startDate`, `baseSalary`) VALUES
(2, 'Zana', '1-1-1981', 'Sulaimany', '', '', 'Software Department', '23432536', 'khalid.hassan@gmx.com', 5, 3, 3, NULL, '', '', '', '', '', '', 3600000),
(3, 'Khalid Ahmed', '1-1-1981', 'Erbil', '', '', 'Software Department', '9647702114288', 'khalid.hassan@gmx.com', 5, 3, 3, NULL, '', '', '', '', '', '', 3600000),
(4, 'Karzan', '1-1-1981', 'Duhok', '', '', 'Hi Hi', '9647702114288', 'khalid.hassan@gmx.com', 8, 3, 6, 5, '', '', '', '', '', '', 3600000),
(6, 'Baban Umar', '1-1-1981', 'Erbil', '', '', 'Software Department', '9647702114288', 'khalid.hassan@gmx.com', 5, 3, 3, 7, '', '4', '', '3', '', '', 3600000);

-- --------------------------------------------------------

--
-- Table structure for table `file`
--

CREATE TABLE IF NOT EXISTS `file` (
  `idFile` int(11) NOT NULL AUTO_INCREMENT,
  `idEmployee` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `path` varchar(255) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`idFile`),
  KEY `fk_File_Employee1` (`idEmployee`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=8 ;

--
-- Dumping data for table `file`
--

INSERT INTO `file` (`idFile`, `idEmployee`, `name`, `path`, `title`, `description`) VALUES
(3, 4, 'jrshkq.jpg', 'o6pc21ag', '54', '4545'),
(4, 4, '545sjn.jpg', 'u50boo55', 'image', 'sdf '),
(5, 4, '2uqgxq.jpg', 'xykmmk9b', 'jquery tweets', ''),
(6, 4, 'r9p3x7.jpg', 'm9n5x34t', 'Xilisoft Video Converter Ultimate', ''),
(7, 6, 'vdgjn9.jpg', 'gwyov8kn', 'Pic', 'poc');

-- --------------------------------------------------------

--
-- Table structure for table `jobtile`
--

CREATE TABLE IF NOT EXISTS `jobtile` (
  `idJobTile` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`idJobTile`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=5 ;

--
-- Dumping data for table `jobtile`
--

INSERT INTO `jobtile` (`idJobTile`, `title`) VALUES
(2, 'Sidra'),
(3, 'Elaf'),
(4, '17491749');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL,
  `password` varchar(100) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `role` varchar(10) DEFAULT NULL,
  `department` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `fk_User_Department1` (`department`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=11 ;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`ID`, `username`, `password`, `name`, `role`, `department`) VALUES
(6, 'admin', 'c93ccd78b2076528346216b3b2f701e6', 'Administrator', 'admin', 3),
(9, 'karzan', 'aef855a3ae550b30bda5451cdfbc5b01', 'Karzan', 'user', 3),
(10, 'polla', '1ca13cec096718ac8ef09c57d9cd5d07', 'polla', 'user', 3);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `employee`
--
ALTER TABLE `employee`
  ADD CONSTRAINT `fk_Employee_cirtificate` FOREIGN KEY (`lastCirtificate`) REFERENCES `cirtificate` (`idcirtificate`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Employee_Department1` FOREIGN KEY (`department`) REFERENCES `department` (`idDepartment`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Employee_File1` FOREIGN KEY (`picture`) REFERENCES `file` (`idFile`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Employee_JobTile1` FOREIGN KEY (`jobTile`) REFERENCES `jobtile` (`idJobTile`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `file`
--
ALTER TABLE `file`
  ADD CONSTRAINT `fk_File_Employee1` FOREIGN KEY (`idEmployee`) REFERENCES `employee` (`idEmployee`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `user`
--
ALTER TABLE `user`
  ADD CONSTRAINT `fk_User_Department1` FOREIGN KEY (`department`) REFERENCES `department` (`idDepartment`) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
