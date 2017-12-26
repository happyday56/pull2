package com.lgh.pull2.service.impl;

import com.lgh.pull.entity.*;
import com.lgh.pull.repository.*;
import com.lgh.pull.service.*;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hot on 2017/6/16.
 */
@Service
public class PushServiceImpl implements PushService {
    private Log log = LogFactory.getLog(PushServiceImpl.class);

    @Autowired
    private WebDriverService webDriverService;

    @Autowired
    private SourceArticleRepository sourceArticleRepository;

    @Autowired
    private ArchivesRepository archivesRepository;


    @Autowired
    private AddOnArticleRepository addOnArticleRepository;

    @Autowired
    private PullService pullService;


    @Autowired
    private SourceArticleThreeRepository sourceArticleThreeRepository;

    @Autowired
    private SourceArticleFourRepository sourceArticleFourRepository;

    @Autowired
    private SourceScoreRepository sourceScoreRepository;

    @Autowired
    private UtilsService utilsService;

    @Autowired
    private ZmzService zmzService;

    /**
     * 处理推荐
     * todo
     * http://cn163.net/archives/4254/
     * http://cn163.net/ads/300.js
     * <p>
     * <div id="entry">
     * <div class="ad_r">
     * <script type="text/javascript" language="javascript" src="http://cn163.net/ads/300.js"></script></div> <p>英文译名Baby Daddy，第4季(2014)ABC.<br>
     * 本季看点：《少男奶爸》讲述的是二十多的小伙子Ben是个酒保,突然在有一天发现前女友把他们激情后的结晶,一位女婴,留在了他租住的房门外。一位各方面都不成熟的小年轻突然要承担起为人之父的职责。经过一番思想斗争,他决定抚养起这个孩子。育儿故事就此展开。<br>
     * -<br>
     * 网友评论：属于表演和剧情都比较浮夸的家庭笑闹剧，但是演员们都还算得上俊男靓女，尤其是小东西非常可爱。奶爸的喜剧，没有多大亮点，中规中矩，插科打诨，嬉笑怒骂，和《<a href="http://cn163.net/archives/1819/">家有喜旺</a>》是两种感觉，这剧偏搞笑些，尤其是Tucker和Bonnie。</p>
     * <p>相关：《<a href="http://cn163.net/archives/1904/">少男奶爸第一季</a>》《<a href="http://cn163.net/archives/1906/">少男奶爸第二三季</a>》<br>
     * <img class="alignnone size-full wp-image-10433" src="https://farm2.staticflickr.com/1658/25068580270_228fc2e974.jpg" alt="少男奶爸" original="https://farm2.staticflickr.com/1658/25068580270_228fc2e974.jpg" height="405" width="270"><br>
     * 播出：ABC Family 类 型：喜剧<br>
     * 地区：美国 主演：让-卢克·比洛多、切尔西·斯特伯<br>
     * 语言：英语 首播日期：2012-06-20 周三<br>
     * 英文：Baby Daddy 别名：少男老爸 第四季<br>
     * 类似推荐《<a href="http://cn163.net/archives/2344/">查莉成长日记</a>》<br>
     * 高清美剧全集迅雷下载地址-本季集数：22<br>
     * 在下面链接上点击或右键选择使用迅雷下载即可<br>
     * 3种格式MKV、MP4是带双语字幕<br>
     * Baby Daddy 第四季<br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E01.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247383064|2352e6ee2b2d6f1fba10aa1f6e42a611|h=4547d5nczd7ecxopeidpf5rrskdjkos7|/">第01集.HD1024.mkv</a>—<a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E01.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|125137239|5bde20b50caa0d4ce3f8feaf8a1868f1|h=xfqazxmyapk7cycxnjogpkkgkzabmzgw|/">MP4</a>—<a href="ed2k://|file|Baby.Daddy.S04E01.720p.HDTV.x264-KILLERS.mkv|754320824|E32DA02A9D175281653064E045FB7048|h=U7GT534LGFGGJ2KSEGFT2QWFHUD76UQ4|/">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E02.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247021871|95c973b2538d147bca2e92a072ee7540|h=kcnl2hnralluuwe4vxxxxbajmnch7sq3|/">第02集.HD1024.mkv</a>—<a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E02.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124564573|ee7402254683d1e056eff2e5b364603f|h=7yeltkn2htzs5prd6iznwiegul3tuclq|/">MP4</a>—<a href="magnet:?xt=urn:btih:142172524cf1995bfc3450e494cdae5c7621e528&amp;dn=Baby.Daddy.S04E02.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E03.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247414865|0631736079a35974a7aeea63bf6a26ac|h=abbpw32ghkrgz4wrq36f6nfe2af7sb3m|/">第03集.HD1024.mkv</a>—<a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E03.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124777557|fd7bc969ac61c9ec424059766890a507|h=hcwc5rngqhh6e7apzau7sxjljtrisph2|/">MP4</a>—<a href="magnet:?xt=urn:btih:fe6013ff24e434b708ca02257872b2af3fd455e4&amp;dn=Baby.Daddy.S04E03.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E04.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|267490120|021a7dfed2755288cc8201ec24ecc132|h=67ujfb3e7hpmkhbeuohh3bgdi6zo7kxt|/">第04集.HD1024.mkv</a>—<a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E04.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|134887050|f2cf1cda8684c46056c721be0c45f06f|h=lfxkcpvncbr4czvquziwv5rftwi4m5qp|/">MP4</a>—<a href="magnet:?xt=urn:btih:8a0b4ed76ccfcc28f51b0a3b8e636a782714f36b&amp;dn=Baby.Daddy.S04E04.720p.HDTV.x264-IMMERSE%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E05.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247382020|eb4c2c43678f5156799595472b1f95e2|h=axxqfthozkrknps5h3bbrxvwef5o4mgr|/">第05集.HD1024.mkv</a>—<a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E05.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124743698|afbecb0424057f293928b0e5b0fa7d74|h=qfg2eolebfzvuwbtckmb75yyhoah6bzl|/">MP4</a>—<a href="magnet:?xt=urn:btih:4d590eefd45046ac3c3646cec3c04e23b8e1ac67&amp;dn=Baby.Daddy.S04E05.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E06.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247281129|598dae05acc25314a922a4f159837913|h=c6h32vuq4gzb7adbsxbplwleinwnaw2u|/">第06集.HD1024.mkv</a>—<a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E06.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124688504|21ec3b3cb33fa0c29c6bfb8447e188ab|h=sxl5ycrfmyab652rv655t725f5gj5x54|/">MP4</a>—<a href="magnet:?xt=urn:btih:22859ba077028782d22234f528265ee60deb8366&amp;dn=Baby.Daddy.S04E06.REAL.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E07.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247370954|af50736c3ae8622e90a25c540c681350|h=z5qfr7ovwutuhcqlvmlp4yk5q5dh5uvt|/">第07集.HD1024.mkv</a>—<a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E07.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124768086|608c1953ac2cdef81796db51b8fc714a|h=vcukvkugosvzpitoykepziwxelh4s7nb|/">MP4</a>—<a href="magnet:?xt=urn:btih:6a13c5d7ae17f2cc226bb3f92f92ff8bae3ee5f9&amp;dn=Baby.Daddy.S04E07.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E08.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247399316|0c0cc3b3e8aa4458f3daf65ee75b119d|h=bwe3jjdzteffky4hqdninfz3tpn2ns3r|/">第08集.HD1024.mkv</a>—<a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E08.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124761543|ca949de495002e591f6796022ce70b17|h=nkq5vv4gx56obt45ukrzcfycwoavbgmq|/">MP4</a>—<a href="magnet:?xt=urn:btih:b85e839966c5b69448b1aad61708a225ec1404ef&amp;dn=Baby.Daddy.S04E08.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E09.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247375011|50bdbf7e2c8f1f46a76f34f24b5d9237|h=bmf6jutdagms4ybhxyfseyomr2k56edf|/">第09集.HD1024.mkv</a>—<a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E09.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124783876|28df2e1e48950811fe0ad9532c782ecf|h=jnlofx5olu534uiwgabzuz4o3bpznd4y|/">MP4</a>—<a href="magnet:?xt=urn:btih:b1d2c98562ea09f48e4ec55e1d627ac5471046e6&amp;dn=Baby.Daddy.S04E09.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E10.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247348272|e91dbd45352b87e53d48b870c86b7dc5|h=vdbx3zv3p5t3amgjmbjjyta4z6adujkn|/">第10集.HD1024.mkv</a>—<a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E10.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124786569|e762fe4cd4c0ce32a03dca69757a92ee|h=vwkpgmkaceahkfyhb7fgbvk74u5cvxpn|/">MP4</a>—<a href="magnet:?xt=urn:btih:175db6c3ec2babd7aab6f9e4e336f6f7a315dd2f&amp;dn=Baby.Daddy.S04E10.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E11.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|248070490|faf2ea6891c7f6e089f49c3b34ce5f59|h=kotkscsnnj7osbg24e7iulmqwilixdnk|/">第11集.HD1024.mkv</a>—<a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E11.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|125132399|25a53c6355216cd04379e277887063e9|h=kgmiahciolirscr2mf5fglvhgdj76gei|/">MP4</a>—<a href="magnet:?xt=urn:btih:63c69afe3b13a84d225c84fd8af75687947652b9&amp;dn=Baby.Daddy.S04E11.720p.HDTV.x264-IMMERSE%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E12.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247505202|3d778c4e82327a0220667762c010bcb7|h=yhhgwz5nlvl5ehq227pmpqel5phazw2r|/">第12集.HD1024.mkv</a>—<a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E12.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124860561|3c66b45e318630fd6faadd3fce2c7d8e|h=o2ny7mtnfqunzv6f2pskm76lzzvsgevv|/">MP4</a>—<a href="magnet:?xt=urn:btih:d4a9f9ff26e51dc5e32923372214c3a76f324ff3&amp;dn=Baby.Daddy.S04E12.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E13.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247338308|eee4e63e75315a2c0f0103bf076f2842|h=vptrh2mq2eodfdg25v3ft6hnvr2nce27|/">第13集.HD1024.mkv</a>—<a href="magnet:?xt=urn:btih:3f9fdf50ccd7b1753fdca2ab71648dea1b0704a9&amp;dn=Baby+Daddy+S04E13+HDTV+x264-ASAP%5Bettv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">MP4</a>—<a href="magnet:?xt=urn:btih:2d7b9ebeb051f3b5fcac3629da40a53716dbb592&amp;dn=Baby.Daddy.S04E13.720p.HDTV.x264-IMMERSE%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E14.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247683603|7bd0b699499759ad63431d6f64314532|h=pm63vbjkfdfzk4b44x2r3jkf7v72h7rq|/">第14集.HD1024.mkv</a>—<a href="magnet:?xt=urn:btih:64809173a73cc8a3395ba401d4d82f9c88ebd61b&amp;dn=Baby.Daddy.S04E14.HDTV.x264-ASAP%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">MP4</a>—<a href="magnet:?xt=urn:btih:bfa53bdcca3edf2fa124be0d0bc724a72c468dc4&amp;dn=Baby.Daddy.S04E14.720p.HDTV.x264-IMMERSE%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E15.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247442354|29c34a3c141af4ed90c3125eb995a2f4|h=wnb4rbddc6z2fezoxtje73pk4bmd4azf|/">第15集.HD1024.mkv</a>—<a href="magnet:?xt=urn:btih:cec7541868d4a00c4add544ea1254c652ee9f480&amp;dn=Baby.Daddy.S04E15.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">MP4</a>—<a href="magnet:?xt=urn:btih:6158109409036b760b2774ce65c3a094602a6053&amp;dn=Baby.Daddy.S04E15.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E16.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247439575|f3c6782bfcc428187bea0c24816ec44a|h=q45oipcsi5zwg53imkia3arrfga5fk7n|/">第16集.HD1024.mkv</a>—<a href="magnet:?xt=urn:btih:c8065dea162a66899b7aea8460f0abd680b13791&amp;dn=Baby.Daddy.S04E16.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">MP4</a>—<a href="magnet:?xt=urn:btih:6d3f23d3529153a02bd85eb95b84a57a9d1160b5&amp;dn=Baby.Daddy.S04E16.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E17.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247469488|c7adab53911c94ae8785519d2ed41e19|h=37e3s5bl57jqtxde4ydfruaia32s5qo7|/">第17集.HD1024.mkv</a>—<a href="magnet:?xt=urn:btih:97d17c292f6e10b195ad5ab0842ede63f1bc05b9&amp;dn=Baby.Daddy.S04E17.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">MP4</a>—<a href="magnet:?xt=urn:btih:f31ac952600832db64a7781dccd04c37f3a12002&amp;dn=Baby.Daddy.S04E17.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E18.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247241927|21eafc668acd4b423b727711f9811a29|h=silikdj7cypycxabxofpjsabifquza23|/">第18集.HD1024.mkv</a>—<a href="magnet:?xt=urn:btih:5e571b8decbd0f6c9a62d5939bf50bf72289ca65&amp;dn=Baby+Daddy+S04E18+HDTV+x264-KILLERS%5Bettv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">MP4</a>—<a href="magnet:?xt=urn:btih:d5a23e11ee603c6c088073f13723603153d3fe97&amp;dn=Baby.Daddy.S04E18.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E19.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247651880|4a01b2bdc12a10fe0ba08769e0c328a3|h=2rdof2hmqw72vvd2eshcl4mgc7vlv4tq|/">第19集.HD1024.mkv</a>—<a href="magnet:?xt=urn:btih:b135f0b96598bf365e4add8938c89afa62f0e21c&amp;dn=Baby.Daddy.S04E19.HDTV.x264-ASAP%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">MP4</a>—<a href="magnet:?xt=urn:btih:da1d3c9e15c4899a2589b89c0acce5e639ad6aa3&amp;dn=Baby.Daddy.S04E19.720p.HDTV.x264-IMMERSE%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E20.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247525424|1293afa233ae0a6609def300d8043b0f|h=joswatsjeokce36x26lrgvbrsuzdzlvw|/">第20集.HD1024.mkv</a>—<a href="magnet:?xt=urn:btih:80c43cdf200e540d1253aa1232d3b4ae1efbe285&amp;dn=Baby+Daddy+S04E20+HDTV+x264-KILLERS%5Bettv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">MP4</a>—<a href="magnet:?xt=urn:btih:f32eae3aebbb2bc4472a3f513e2247fea559736b&amp;dn=Baby.Daddy.S04E20.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E21.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247502880|0a7b7adfa567d45ddae773045c367150|h=5sj6zzgebxvspajv6gnwjy63z7jnycfs|/">第21集.HD1024.mkv</a>—<a href="magnet:?xt=urn:btih:f758bc4a31c9306c2b0b0695f6f105bee95073d7&amp;dn=Baby.Daddy.S04E21.HDTV.x264-KILLERS%5Bettv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">MP4</a>—<a href="magnet:?xt=urn:btih:29b279d9ed37bf70bf37cbbffdf0d827a2c7c0db&amp;dn=Baby.Daddy.S04E21.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E22.End.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247545563|c475debb2495bcb97c555dc0ff00ef87|h=6q6q5435aob3b7wzn547mzbeauideygu|/">第22集.HD1024.mkv</a>—<a href="magnet:?xt=urn:btih:29d5a300ed8fe2c05b46a49ce35188c9e3537801&amp;dn=Baby.Daddy.S04E22.HDTV.x264-ASAP%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">MP4</a>—<a href="magnet:?xt=urn:btih:6a768e5ffb7a9d9dec0511de3bd6767c486736c1&amp;dn=Baby.Daddy.S04E22.720p.HDTV.x264-IMMERSE%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <strong>少男老爸第五季,每周四播（本季终）</strong><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E01.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|209727014|7111fb84370664c3b390cfd97e9bffcf|h=serl4xrdjbor2nedup573ysfvyjyxrkp|/">S05E01.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:d1ec078d486cefc6abe19e54286dd254f101346f&amp;dn=Baby.Daddy.S05E01.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E02.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|211685924|198545727ac9249a969338612fd3660f|h=dtnnaa6zu7ohdqi5ch4ydzz67wt424wu|/">S05E02.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:2fcbac7760b0f61eb2b995065fe01eca7b29ee9a&amp;dn=Baby.Daddy.S05E02.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E03.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.v2.mp4|213070111|82fe9a64a04379f4f62df945ce5fae67|h=57wvjcarlbgrdt5cz7zxncxrsj7ab7vp|/">S05E03.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:5367f92c52ccecaa68954efb074de67448bc7b44&amp;dn=Baby.Daddy.S05E03.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E04.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|211253020|4458ad656d080c569e7cacc1679c591f|h=pmrd6isz4pdcowmg3ge3xd2u56vj5mok|/">S05E04.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:88bac96b288c7149c75c4d788b33b0ce84e582bd&amp;dn=Baby.Daddy.S05E04.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E05.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|211203738|58f80c8242800c582d91e676d5d7694c|h=633wzubmd2uo4ehyn7ebl5vczzhf6tmj|/">S05E05.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:4757e5fc5930b29db811b67069a4bb1258421870&amp;dn=Baby.Daddy.S05E05.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E06.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|210566227|3586a7d0be734de6455e5e6a82b6f265|h=ehpt2cvtslvevfdinflw7hqzrzy4ylle|/">S05E06.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:e771fcb928fb43959d6ca0709a5bc58f1a54a4eb&amp;dn=Baby.Daddy.S05E06.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E07.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|212136117|1abe70024eb736bca9c00229e63fb309|h=mfmfmsnaprynobhibqxzxnkjmskhur4n|/">S05E07.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:bf0c76eb4078dbf398771ca39ae243edce46f756&amp;dn=Baby.Daddy.S05E07.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E08.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|209118734|b40d04939a8208e87bf069d7688f3b69|h=okixj6wh3p2b7dlnaf3i225ca46ob22r|/">S05E08.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:89a12e2f4b3d6deb8688f71f1c62f2841caa600c&amp;dn=Baby.Daddy.S05E08.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|Baby.Daddy.S05E09.%E5%8F%8C%E8%AF%AD%E5%AD%97%E5%B9%95.720p.UnIon%E5%AD%97%E5%B9%95%E7%BB%84.mkv|375116308|6ecef093f233e5ffa59f7e4e04871963|h=56wxa2pl2t55d72xyriyb5i6nuvaaj75|/">S05E09.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:a079af315ec4f09dba650a6e38a5e807b5a2c261&amp;dn=Baby.Daddy.S05E09.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|Baby.Daddy.S05E10.%E5%8F%8C%E8%AF%AD%E5%AD%97%E5%B9%95.720p.UnIon%E5%AD%97%E5%B9%95%E7%BB%84.mkv|350882410|168cb96e63363e51fba18dbc4de29953|h=6uzf7cfoxur3lyewobri2ruyekqrkghg|/">S05E10.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:ed897bd75d84d7aa336600ccbdcaf2a42e772306&amp;dn=Baby.Daddy.S05E10.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E11.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|209820128|363c7de9d615333d4cca5ab0d2269a86|h=qydkiygxplfxkr5ej75ycrjsg2sieicj|/">S05E11.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:f8bbf1bd86eff8553956786cbf93a0ba4be05e38&amp;dn=Baby.Daddy.S05E11.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E12.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|210424659|8bae6afb99b4f963162a00ce6ea50407|h=usbvk7hnq4e5zloligdm4dbnfl7dy6hn|/">S05E12.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:8191b5a6995be72978a770e23d24684490366dcd&amp;dn=Baby.Daddy.S05E12.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|Baby.Daddy.S05E13.%E5%8F%8C%E8%AF%AD%E5%AD%97%E5%B9%95.720p.TVrip.x264-UnIon%E5%AD%97%E5%B9%95%E7%BB%84.mkv|482364360|49b8a233fccaf6218223e5c0762a7671|h=5woop6tmuhjj2vafxx25qz3sfcjuewt3|/">S05E13.字幕版-HDtv.mp4</a>｜<a href="ed2k://|file|Baby.Daddy.S05E13.720p.HDTV.x264-AVS.mkv|654326604|9DE9F8EE205A17C858C025ABD78A6B41|h=REEGFPJ56K6HNQYCH2MF6AZXTCEVB7PY|/">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E14.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.V2.mp4|208152370|6452bee03a8b0b4f26b1f25281a1ab74|h=7zr32i5tgltgxejw53np4pprtwaez346|/">S05E14.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:ffa64c8128dbe1a2d2b1af7470eb508a783f32da&amp;dn=Baby.Daddy.S05E14.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E15.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|206068934|13d70a266419fe1f3728698e4a719f76|h=nrlz7n42ud4qteeljp72ilegvxrgzfbx|/">S05E15.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:5878aa798926af83910e84319ad59741eb365472&amp;dn=Baby.Daddy.S05E15.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|Baby.Daddy.S05E16.%E5%8F%8C%E8%AF%AD%E5%AD%97%E5%B9%95.720p.TVrip.x264-UnIon%E5%AD%97%E5%B9%95%E7%BB%84.mkv|570758454|77db6464d352c47879dbd5298430bdfb|h=u42yg52lphmxeadzofef6i7znuqau457|/">S05E16.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:91673c7cee8ca5f62b66de46a1678b838a092a95&amp;dn=Baby.Daddy.S05E16.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E17.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|208053126|94c7e7b07e59bc585f59137be73fbb91|h=owpn6csqnxvuqoxmb33ymqqvgn5upls2|/">S05E17.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:8607076ec3d333e0a5ea2805fd5b89e0e2a27568&amp;dn=Baby.Daddy.S05E17.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E18.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|205016433|4b8b85cf294011e6dad22e86c9f139c2|h=fw6g6eri6totzk556heafsboqtmwd6bu|/">S05E18.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:d46c091d5585a7204cf33cd08d8810977412c85e&amp;dn=Baby.Daddy.S05E18.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E19.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|206283218|80c53d9f7d0c1390f6171790ac4d1d29|h=q75vzvnfw5jetjkpok5kfgpo3dbhl5zp|/">S05E19.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:d8b2243d0841180be82da45974b449ace1cc2622&amp;dn=Baby.Daddy.S05E19.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E20.END.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|209198388|670ad82b150707548c0ca21fff473886|h=yn73hwoopt7dfi5awgynb3sdk5f6ywle|/">S05E20.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:6c447e41a3595ff5d28766c000ea60b4a182766e&amp;dn=Baby.Daddy.S05E20.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <strong>少男奶爸第六季</strong>,（2017年3月14回归）<br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E01.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P.mp4|242313211|b9c509dbc21de4faefa45cc2b813841c|h=juo6q7xpoetsebocn3kik7z653nwt4fw|/">S06E01.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:8a3d8dccbd40f776da91e51b7cd9e527f39fa1a7&amp;dn=Baby.Daddy.S06E01.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E02.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P.mp4|235136203|b0541505a0e008cccea8730b7ae2d6e9|h=zujh3knzregjqhw3sgcg7y4tm4i4ps3z|/">S06E02.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:3675d201fe28b1f6254d5504c0bb13295d14ede7&amp;dn=Baby.Daddy.S06E02.PROPER.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A271">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E03.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P.mp4|241170735|6cb7545e27ea8c58c56e6206b9e4fde8|h=pzcj6gq6yqxzw2rrq5btpkguv3cma6iz|/">S06E03.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:67ae2270d81be34b1ed053c8bf0c5b2111e4e0d4&amp;dn=Baby.Daddy.S06E03.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E04.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P.mp4|243132727|64915d005f0a1f6b8e69208d57360d52|h=w6uqu55stxbslr7z5eywqrh2oqkxfv77|/">S06E04.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:cdd1b1aa48280ea88a9619662661648edf1c97f4&amp;dn=Baby.Daddy.S06E04.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E05.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P.mp4|242841693|7e36b6329c8a68d782a18cbb9b7ae505|h=u2fzocnmluj55m5n5zomnsnaonp5korq|/">S06E05.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:cdd1b1aa48280ea88a9619662661648edf1c97f4&amp;dn=Baby.Daddy.S06E04.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E06.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P-%E4%BA%BA%E4%BA%BA%E5%BD%B1%E8%A7%86.mp4|243311550|23042663b49932d7ebce650673a408af|h=okea75vvqa5hattaeaqplihzm5mgtu5i|/">S06E06.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:36dc2b2738a35d33037e9167891ce00f59660360&amp;dn=Baby.Daddy.S06E06.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E07.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P-%E4%BA%BA%E4%BA%BA%E5%BD%B1%E8%A7%86.mp4|244361681|37766d6be63ab249e4053a3d649fa198|h=qyus3qm4inzpeijbo3ii6bzawrl2dc2d|/">S06E07.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:81e5e9e4ca2aa2ecd65eb8a5ea8769c7bd807515&amp;dn=Baby.Daddy.S06E07.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E08.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P-%E4%BA%BA%E4%BA%BA%E5%BD%B1%E8%A7%86.mp4|275487624|54e234413668739fa7e6ca39ec2e4a93|h=zqjnnn4oa3nahopxwsctk3adi5wuql4i|/">S06E08.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:a632a68b1f86d38b2e7db60962885a13f777efd0&amp;dn=Baby.Daddy.S06E08.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E09.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P-%E4%BA%BA%E4%BA%BA%E5%BD%B1%E8%A7%86.mp4|254846637|a06f0196d8198e23583ae2e65c33ffdd|h=zxwb44ys3awphn75effkc7cxw7dgu5yn|/">S06E09.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:2db9bbf50947e1b6350641c65fced3954c7ffc66&amp;dn=Baby.Daddy.S06E09.720p.WEB.x264-TBS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * <a href="ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E10.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P-%E4%BA%BA%E4%BA%BA%E5%BD%B1%E8%A7%86.mp4|256213332|bf79be1fd1852fcd764d97a8bb6654d8|h=2wmuew3n3mnvsv5fl4o54zenfkewgayj|/">S06E10.字幕版-HDtv.mp4</a>｜<a href="magnet:?xt=urn:btih:752a0e4808d800a4d64629dd92bafe1a9fbc4067&amp;dn=Baby.Daddy.S06E10.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * S06E11.字幕版-HDtv.mp4｜<a href="magnet:?xt=urn:btih:831e843eaef77e4e3f7abd4b86e370e9fede0b51&amp;dn=Baby.Daddy.S06E11.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710">720P</a><br>
     * S06E12.字幕版-HDtv.mp4｜720P<br>
     * S06E13.字幕版-HDtv.mp4｜720P<br>
     * S06E14.字幕版-HDtv.mp4｜720P<br>
     * S06E15.字幕版-HDtv.mp4｜720P<br>
     * S06E16.字幕版-HDtv.mp4｜720P<br>
     * S06E17.字幕版-HDtv.mp4｜720P<br>
     * S06E18.字幕版-HDtv.mp4｜720P<br>
     * S06E19.字幕版-HDtv.mp4｜720P<br>
     * S06E20.字幕版-HDtv.mp4｜720P</p>
     * </div>
     */
//    @Scheduled(initialDelay = 1000, fixedDelay = 1000 * 3600 * 24 * 365)
    public void doRecommend() {
//        String template = "\n" +
//                "<div id=\"entry\">\n" +
//                "<div class=\"ad_r\">\n" +
//                "<script type=\"text/javascript\" language=\"javascript\" src=\"http://cn163.net/ads/300.js\"></script></div> <p>英文译名Baby Daddy，第4季(2014)ABC.<br>\n" +
//                "本季看点：《少男奶爸》讲述的是二十多的小伙子Ben是个酒保,突然在有一天发现前女友把他们激情后的结晶,一位女婴,留在了他租住的房门外。一位各方面都不成熟的小年轻突然要承担起为人之父的职责。经过一番思想斗争,他决定抚养起这个孩子。育儿故事就此展开。<br>\n" +
//                "-<br>\n" +
//                "网友评论：属于表演和剧情都比较浮夸的家庭笑闹剧，但是演员们都还算得上俊男靓女，尤其是小东西非常可爱。奶爸的喜剧，没有多大亮点，中规中矩，插科打诨，嬉笑怒骂，和《<a href=\"http://cn163.net/archives/1819/\">家有喜旺</a>》是两种感觉，这剧偏搞笑些，尤其是Tucker和Bonnie。</p>\n" +
//                "<p>相关：《<a href=\"http://cn163.net/archives/1904/\">少男奶爸第一季</a>》《<a href=\"http://cn163.net/archives/1906/\">少男奶爸第二三季</a>》<br>\n" +
//                "<img class=\"alignnone size-full wp-image-10433\" src=\"https://farm2.staticflickr.com/1658/25068580270_228fc2e974.jpg\" alt=\"少男奶爸\" original=\"https://farm2.staticflickr.com/1658/25068580270_228fc2e974.jpg\" height=\"405\" width=\"270\"><br>\n" +
//                "播出：ABC Family 类 型：喜剧<br>\n" +
//                "地区：美国 主演：让-卢克·比洛多、切尔西·斯特伯<br>\n" +
//                "语言：英语 首播日期：2012-06-20 周三<br>\n" +
//                "英文：Baby Daddy 别名：少男老爸 第四季<br>\n" +
//                "类似推荐《<a href=\"http://cn163.net/archives/2344/\">查莉成长日记</a>》<br>\n" +
//                "高清美剧全集迅雷下载地址-本季集数：22<br>\n" +
//                "在下面链接上点击或右键选择使用迅雷下载即可<br>\n" +
//                "3种格式MKV、MP4是带双语字幕<br>\n" +
//                "Baby Daddy 第四季<br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E01.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247383064|2352e6ee2b2d6f1fba10aa1f6e42a611|h=4547d5nczd7ecxopeidpf5rrskdjkos7|/\">第01集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E01.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|125137239|5bde20b50caa0d4ce3f8feaf8a1868f1|h=xfqazxmyapk7cycxnjogpkkgkzabmzgw|/\">MP4</a>—<a href=\"ed2k://|file|Baby.Daddy.S04E01.720p.HDTV.x264-KILLERS.mkv|754320824|E32DA02A9D175281653064E045FB7048|h=U7GT534LGFGGJ2KSEGFT2QWFHUD76UQ4|/\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E02.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247021871|95c973b2538d147bca2e92a072ee7540|h=kcnl2hnralluuwe4vxxxxbajmnch7sq3|/\">第02集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E02.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124564573|ee7402254683d1e056eff2e5b364603f|h=7yeltkn2htzs5prd6iznwiegul3tuclq|/\">MP4</a>—<a href=\"magnet:?xt=urn:btih:142172524cf1995bfc3450e494cdae5c7621e528&amp;dn=Baby.Daddy.S04E02.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E03.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247414865|0631736079a35974a7aeea63bf6a26ac|h=abbpw32ghkrgz4wrq36f6nfe2af7sb3m|/\">第03集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E03.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124777557|fd7bc969ac61c9ec424059766890a507|h=hcwc5rngqhh6e7apzau7sxjljtrisph2|/\">MP4</a>—<a href=\"magnet:?xt=urn:btih:fe6013ff24e434b708ca02257872b2af3fd455e4&amp;dn=Baby.Daddy.S04E03.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E04.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|267490120|021a7dfed2755288cc8201ec24ecc132|h=67ujfb3e7hpmkhbeuohh3bgdi6zo7kxt|/\">第04集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E04.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|134887050|f2cf1cda8684c46056c721be0c45f06f|h=lfxkcpvncbr4czvquziwv5rftwi4m5qp|/\">MP4</a>—<a href=\"magnet:?xt=urn:btih:8a0b4ed76ccfcc28f51b0a3b8e636a782714f36b&amp;dn=Baby.Daddy.S04E04.720p.HDTV.x264-IMMERSE%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E05.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247382020|eb4c2c43678f5156799595472b1f95e2|h=axxqfthozkrknps5h3bbrxvwef5o4mgr|/\">第05集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E05.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124743698|afbecb0424057f293928b0e5b0fa7d74|h=qfg2eolebfzvuwbtckmb75yyhoah6bzl|/\">MP4</a>—<a href=\"magnet:?xt=urn:btih:4d590eefd45046ac3c3646cec3c04e23b8e1ac67&amp;dn=Baby.Daddy.S04E05.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E06.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247281129|598dae05acc25314a922a4f159837913|h=c6h32vuq4gzb7adbsxbplwleinwnaw2u|/\">第06集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E06.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124688504|21ec3b3cb33fa0c29c6bfb8447e188ab|h=sxl5ycrfmyab652rv655t725f5gj5x54|/\">MP4</a>—<a href=\"magnet:?xt=urn:btih:22859ba077028782d22234f528265ee60deb8366&amp;dn=Baby.Daddy.S04E06.REAL.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E07.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247370954|af50736c3ae8622e90a25c540c681350|h=z5qfr7ovwutuhcqlvmlp4yk5q5dh5uvt|/\">第07集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E07.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124768086|608c1953ac2cdef81796db51b8fc714a|h=vcukvkugosvzpitoykepziwxelh4s7nb|/\">MP4</a>—<a href=\"magnet:?xt=urn:btih:6a13c5d7ae17f2cc226bb3f92f92ff8bae3ee5f9&amp;dn=Baby.Daddy.S04E07.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E08.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247399316|0c0cc3b3e8aa4458f3daf65ee75b119d|h=bwe3jjdzteffky4hqdninfz3tpn2ns3r|/\">第08集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E08.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124761543|ca949de495002e591f6796022ce70b17|h=nkq5vv4gx56obt45ukrzcfycwoavbgmq|/\">MP4</a>—<a href=\"magnet:?xt=urn:btih:b85e839966c5b69448b1aad61708a225ec1404ef&amp;dn=Baby.Daddy.S04E08.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E09.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247375011|50bdbf7e2c8f1f46a76f34f24b5d9237|h=bmf6jutdagms4ybhxyfseyomr2k56edf|/\">第09集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E09.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124783876|28df2e1e48950811fe0ad9532c782ecf|h=jnlofx5olu534uiwgabzuz4o3bpznd4y|/\">MP4</a>—<a href=\"magnet:?xt=urn:btih:b1d2c98562ea09f48e4ec55e1d627ac5471046e6&amp;dn=Baby.Daddy.S04E09.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E10.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247348272|e91dbd45352b87e53d48b870c86b7dc5|h=vdbx3zv3p5t3amgjmbjjyta4z6adujkn|/\">第10集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E10.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124786569|e762fe4cd4c0ce32a03dca69757a92ee|h=vwkpgmkaceahkfyhb7fgbvk74u5cvxpn|/\">MP4</a>—<a href=\"magnet:?xt=urn:btih:175db6c3ec2babd7aab6f9e4e336f6f7a315dd2f&amp;dn=Baby.Daddy.S04E10.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E11.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|248070490|faf2ea6891c7f6e089f49c3b34ce5f59|h=kotkscsnnj7osbg24e7iulmqwilixdnk|/\">第11集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E11.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|125132399|25a53c6355216cd04379e277887063e9|h=kgmiahciolirscr2mf5fglvhgdj76gei|/\">MP4</a>—<a href=\"magnet:?xt=urn:btih:63c69afe3b13a84d225c84fd8af75687947652b9&amp;dn=Baby.Daddy.S04E11.720p.HDTV.x264-IMMERSE%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E12.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247505202|3d778c4e82327a0220667762c010bcb7|h=yhhgwz5nlvl5ehq227pmpqel5phazw2r|/\">第12集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E12.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124860561|3c66b45e318630fd6faadd3fce2c7d8e|h=o2ny7mtnfqunzv6f2pskm76lzzvsgevv|/\">MP4</a>—<a href=\"magnet:?xt=urn:btih:d4a9f9ff26e51dc5e32923372214c3a76f324ff3&amp;dn=Baby.Daddy.S04E12.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E13.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247338308|eee4e63e75315a2c0f0103bf076f2842|h=vptrh2mq2eodfdg25v3ft6hnvr2nce27|/\">第13集.HD1024.mkv</a>—<a href=\"magnet:?xt=urn:btih:3f9fdf50ccd7b1753fdca2ab71648dea1b0704a9&amp;dn=Baby+Daddy+S04E13+HDTV+x264-ASAP%5Bettv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">MP4</a>—<a href=\"magnet:?xt=urn:btih:2d7b9ebeb051f3b5fcac3629da40a53716dbb592&amp;dn=Baby.Daddy.S04E13.720p.HDTV.x264-IMMERSE%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E14.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247683603|7bd0b699499759ad63431d6f64314532|h=pm63vbjkfdfzk4b44x2r3jkf7v72h7rq|/\">第14集.HD1024.mkv</a>—<a href=\"magnet:?xt=urn:btih:64809173a73cc8a3395ba401d4d82f9c88ebd61b&amp;dn=Baby.Daddy.S04E14.HDTV.x264-ASAP%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">MP4</a>—<a href=\"magnet:?xt=urn:btih:bfa53bdcca3edf2fa124be0d0bc724a72c468dc4&amp;dn=Baby.Daddy.S04E14.720p.HDTV.x264-IMMERSE%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E15.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247442354|29c34a3c141af4ed90c3125eb995a2f4|h=wnb4rbddc6z2fezoxtje73pk4bmd4azf|/\">第15集.HD1024.mkv</a>—<a href=\"magnet:?xt=urn:btih:cec7541868d4a00c4add544ea1254c652ee9f480&amp;dn=Baby.Daddy.S04E15.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">MP4</a>—<a href=\"magnet:?xt=urn:btih:6158109409036b760b2774ce65c3a094602a6053&amp;dn=Baby.Daddy.S04E15.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E16.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247439575|f3c6782bfcc428187bea0c24816ec44a|h=q45oipcsi5zwg53imkia3arrfga5fk7n|/\">第16集.HD1024.mkv</a>—<a href=\"magnet:?xt=urn:btih:c8065dea162a66899b7aea8460f0abd680b13791&amp;dn=Baby.Daddy.S04E16.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">MP4</a>—<a href=\"magnet:?xt=urn:btih:6d3f23d3529153a02bd85eb95b84a57a9d1160b5&amp;dn=Baby.Daddy.S04E16.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E17.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247469488|c7adab53911c94ae8785519d2ed41e19|h=37e3s5bl57jqtxde4ydfruaia32s5qo7|/\">第17集.HD1024.mkv</a>—<a href=\"magnet:?xt=urn:btih:97d17c292f6e10b195ad5ab0842ede63f1bc05b9&amp;dn=Baby.Daddy.S04E17.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">MP4</a>—<a href=\"magnet:?xt=urn:btih:f31ac952600832db64a7781dccd04c37f3a12002&amp;dn=Baby.Daddy.S04E17.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E18.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247241927|21eafc668acd4b423b727711f9811a29|h=silikdj7cypycxabxofpjsabifquza23|/\">第18集.HD1024.mkv</a>—<a href=\"magnet:?xt=urn:btih:5e571b8decbd0f6c9a62d5939bf50bf72289ca65&amp;dn=Baby+Daddy+S04E18+HDTV+x264-KILLERS%5Bettv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">MP4</a>—<a href=\"magnet:?xt=urn:btih:d5a23e11ee603c6c088073f13723603153d3fe97&amp;dn=Baby.Daddy.S04E18.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E19.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247651880|4a01b2bdc12a10fe0ba08769e0c328a3|h=2rdof2hmqw72vvd2eshcl4mgc7vlv4tq|/\">第19集.HD1024.mkv</a>—<a href=\"magnet:?xt=urn:btih:b135f0b96598bf365e4add8938c89afa62f0e21c&amp;dn=Baby.Daddy.S04E19.HDTV.x264-ASAP%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">MP4</a>—<a href=\"magnet:?xt=urn:btih:da1d3c9e15c4899a2589b89c0acce5e639ad6aa3&amp;dn=Baby.Daddy.S04E19.720p.HDTV.x264-IMMERSE%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E20.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247525424|1293afa233ae0a6609def300d8043b0f|h=joswatsjeokce36x26lrgvbrsuzdzlvw|/\">第20集.HD1024.mkv</a>—<a href=\"magnet:?xt=urn:btih:80c43cdf200e540d1253aa1232d3b4ae1efbe285&amp;dn=Baby+Daddy+S04E20+HDTV+x264-KILLERS%5Bettv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">MP4</a>—<a href=\"magnet:?xt=urn:btih:f32eae3aebbb2bc4472a3f513e2247fea559736b&amp;dn=Baby.Daddy.S04E20.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E21.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247502880|0a7b7adfa567d45ddae773045c367150|h=5sj6zzgebxvspajv6gnwjy63z7jnycfs|/\">第21集.HD1024.mkv</a>—<a href=\"magnet:?xt=urn:btih:f758bc4a31c9306c2b0b0695f6f105bee95073d7&amp;dn=Baby.Daddy.S04E21.HDTV.x264-KILLERS%5Bettv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">MP4</a>—<a href=\"magnet:?xt=urn:btih:29b279d9ed37bf70bf37cbbffdf0d827a2c7c0db&amp;dn=Baby.Daddy.S04E21.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E22.End.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247545563|c475debb2495bcb97c555dc0ff00ef87|h=6q6q5435aob3b7wzn547mzbeauideygu|/\">第22集.HD1024.mkv</a>—<a href=\"magnet:?xt=urn:btih:29d5a300ed8fe2c05b46a49ce35188c9e3537801&amp;dn=Baby.Daddy.S04E22.HDTV.x264-ASAP%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">MP4</a>—<a href=\"magnet:?xt=urn:btih:6a768e5ffb7a9d9dec0511de3bd6767c486736c1&amp;dn=Baby.Daddy.S04E22.720p.HDTV.x264-IMMERSE%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<strong>少男老爸第五季,每周四播（本季终）</strong><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E01.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|209727014|7111fb84370664c3b390cfd97e9bffcf|h=serl4xrdjbor2nedup573ysfvyjyxrkp|/\">S05E01.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:d1ec078d486cefc6abe19e54286dd254f101346f&amp;dn=Baby.Daddy.S05E01.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E02.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|211685924|198545727ac9249a969338612fd3660f|h=dtnnaa6zu7ohdqi5ch4ydzz67wt424wu|/\">S05E02.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:2fcbac7760b0f61eb2b995065fe01eca7b29ee9a&amp;dn=Baby.Daddy.S05E02.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E03.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.v2.mp4|213070111|82fe9a64a04379f4f62df945ce5fae67|h=57wvjcarlbgrdt5cz7zxncxrsj7ab7vp|/\">S05E03.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:5367f92c52ccecaa68954efb074de67448bc7b44&amp;dn=Baby.Daddy.S05E03.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E04.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|211253020|4458ad656d080c569e7cacc1679c591f|h=pmrd6isz4pdcowmg3ge3xd2u56vj5mok|/\">S05E04.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:88bac96b288c7149c75c4d788b33b0ce84e582bd&amp;dn=Baby.Daddy.S05E04.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E05.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|211203738|58f80c8242800c582d91e676d5d7694c|h=633wzubmd2uo4ehyn7ebl5vczzhf6tmj|/\">S05E05.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:4757e5fc5930b29db811b67069a4bb1258421870&amp;dn=Baby.Daddy.S05E05.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E06.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|210566227|3586a7d0be734de6455e5e6a82b6f265|h=ehpt2cvtslvevfdinflw7hqzrzy4ylle|/\">S05E06.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:e771fcb928fb43959d6ca0709a5bc58f1a54a4eb&amp;dn=Baby.Daddy.S05E06.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E07.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|212136117|1abe70024eb736bca9c00229e63fb309|h=mfmfmsnaprynobhibqxzxnkjmskhur4n|/\">S05E07.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:bf0c76eb4078dbf398771ca39ae243edce46f756&amp;dn=Baby.Daddy.S05E07.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E08.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|209118734|b40d04939a8208e87bf069d7688f3b69|h=okixj6wh3p2b7dlnaf3i225ca46ob22r|/\">S05E08.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:89a12e2f4b3d6deb8688f71f1c62f2841caa600c&amp;dn=Baby.Daddy.S05E08.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|Baby.Daddy.S05E09.%E5%8F%8C%E8%AF%AD%E5%AD%97%E5%B9%95.720p.UnIon%E5%AD%97%E5%B9%95%E7%BB%84.mkv|375116308|6ecef093f233e5ffa59f7e4e04871963|h=56wxa2pl2t55d72xyriyb5i6nuvaaj75|/\">S05E09.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:a079af315ec4f09dba650a6e38a5e807b5a2c261&amp;dn=Baby.Daddy.S05E09.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|Baby.Daddy.S05E10.%E5%8F%8C%E8%AF%AD%E5%AD%97%E5%B9%95.720p.UnIon%E5%AD%97%E5%B9%95%E7%BB%84.mkv|350882410|168cb96e63363e51fba18dbc4de29953|h=6uzf7cfoxur3lyewobri2ruyekqrkghg|/\">S05E10.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:ed897bd75d84d7aa336600ccbdcaf2a42e772306&amp;dn=Baby.Daddy.S05E10.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E11.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|209820128|363c7de9d615333d4cca5ab0d2269a86|h=qydkiygxplfxkr5ej75ycrjsg2sieicj|/\">S05E11.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:f8bbf1bd86eff8553956786cbf93a0ba4be05e38&amp;dn=Baby.Daddy.S05E11.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E12.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|210424659|8bae6afb99b4f963162a00ce6ea50407|h=usbvk7hnq4e5zloligdm4dbnfl7dy6hn|/\">S05E12.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:8191b5a6995be72978a770e23d24684490366dcd&amp;dn=Baby.Daddy.S05E12.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|Baby.Daddy.S05E13.%E5%8F%8C%E8%AF%AD%E5%AD%97%E5%B9%95.720p.TVrip.x264-UnIon%E5%AD%97%E5%B9%95%E7%BB%84.mkv|482364360|49b8a233fccaf6218223e5c0762a7671|h=5woop6tmuhjj2vafxx25qz3sfcjuewt3|/\">S05E13.字幕版-HDtv.mp4</a>｜<a href=\"ed2k://|file|Baby.Daddy.S05E13.720p.HDTV.x264-AVS.mkv|654326604|9DE9F8EE205A17C858C025ABD78A6B41|h=REEGFPJ56K6HNQYCH2MF6AZXTCEVB7PY|/\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E14.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.V2.mp4|208152370|6452bee03a8b0b4f26b1f25281a1ab74|h=7zr32i5tgltgxejw53np4pprtwaez346|/\">S05E14.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:ffa64c8128dbe1a2d2b1af7470eb508a783f32da&amp;dn=Baby.Daddy.S05E14.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E15.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|206068934|13d70a266419fe1f3728698e4a719f76|h=nrlz7n42ud4qteeljp72ilegvxrgzfbx|/\">S05E15.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:5878aa798926af83910e84319ad59741eb365472&amp;dn=Baby.Daddy.S05E15.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|Baby.Daddy.S05E16.%E5%8F%8C%E8%AF%AD%E5%AD%97%E5%B9%95.720p.TVrip.x264-UnIon%E5%AD%97%E5%B9%95%E7%BB%84.mkv|570758454|77db6464d352c47879dbd5298430bdfb|h=u42yg52lphmxeadzofef6i7znuqau457|/\">S05E16.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:91673c7cee8ca5f62b66de46a1678b838a092a95&amp;dn=Baby.Daddy.S05E16.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E17.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|208053126|94c7e7b07e59bc585f59137be73fbb91|h=owpn6csqnxvuqoxmb33ymqqvgn5upls2|/\">S05E17.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:8607076ec3d333e0a5ea2805fd5b89e0e2a27568&amp;dn=Baby.Daddy.S05E17.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E18.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|205016433|4b8b85cf294011e6dad22e86c9f139c2|h=fw6g6eri6totzk556heafsboqtmwd6bu|/\">S05E18.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:d46c091d5585a7204cf33cd08d8810977412c85e&amp;dn=Baby.Daddy.S05E18.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E19.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|206283218|80c53d9f7d0c1390f6171790ac4d1d29|h=q75vzvnfw5jetjkpok5kfgpo3dbhl5zp|/\">S05E19.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:d8b2243d0841180be82da45974b449ace1cc2622&amp;dn=Baby.Daddy.S05E19.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E20.END.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|209198388|670ad82b150707548c0ca21fff473886|h=yn73hwoopt7dfi5awgynb3sdk5f6ywle|/\">S05E20.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:6c447e41a3595ff5d28766c000ea60b4a182766e&amp;dn=Baby.Daddy.S05E20.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<strong>少男奶爸第六季</strong>,（2017年3月14回归）<br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E01.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P.mp4|242313211|b9c509dbc21de4faefa45cc2b813841c|h=juo6q7xpoetsebocn3kik7z653nwt4fw|/\">S06E01.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:8a3d8dccbd40f776da91e51b7cd9e527f39fa1a7&amp;dn=Baby.Daddy.S06E01.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E02.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P.mp4|235136203|b0541505a0e008cccea8730b7ae2d6e9|h=zujh3knzregjqhw3sgcg7y4tm4i4ps3z|/\">S06E02.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:3675d201fe28b1f6254d5504c0bb13295d14ede7&amp;dn=Baby.Daddy.S06E02.PROPER.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A271\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E03.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P.mp4|241170735|6cb7545e27ea8c58c56e6206b9e4fde8|h=pzcj6gq6yqxzw2rrq5btpkguv3cma6iz|/\">S06E03.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:67ae2270d81be34b1ed053c8bf0c5b2111e4e0d4&amp;dn=Baby.Daddy.S06E03.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E04.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P.mp4|243132727|64915d005f0a1f6b8e69208d57360d52|h=w6uqu55stxbslr7z5eywqrh2oqkxfv77|/\">S06E04.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:cdd1b1aa48280ea88a9619662661648edf1c97f4&amp;dn=Baby.Daddy.S06E04.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E05.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P.mp4|242841693|7e36b6329c8a68d782a18cbb9b7ae505|h=u2fzocnmluj55m5n5zomnsnaonp5korq|/\">S06E05.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:cdd1b1aa48280ea88a9619662661648edf1c97f4&amp;dn=Baby.Daddy.S06E04.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E06.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P-%E4%BA%BA%E4%BA%BA%E5%BD%B1%E8%A7%86.mp4|243311550|23042663b49932d7ebce650673a408af|h=okea75vvqa5hattaeaqplihzm5mgtu5i|/\">S06E06.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:36dc2b2738a35d33037e9167891ce00f59660360&amp;dn=Baby.Daddy.S06E06.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E07.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P-%E4%BA%BA%E4%BA%BA%E5%BD%B1%E8%A7%86.mp4|244361681|37766d6be63ab249e4053a3d649fa198|h=qyus3qm4inzpeijbo3ii6bzawrl2dc2d|/\">S06E07.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:81e5e9e4ca2aa2ecd65eb8a5ea8769c7bd807515&amp;dn=Baby.Daddy.S06E07.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E08.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P-%E4%BA%BA%E4%BA%BA%E5%BD%B1%E8%A7%86.mp4|275487624|54e234413668739fa7e6ca39ec2e4a93|h=zqjnnn4oa3nahopxwsctk3adi5wuql4i|/\">S06E08.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:a632a68b1f86d38b2e7db60962885a13f777efd0&amp;dn=Baby.Daddy.S06E08.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E09.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P-%E4%BA%BA%E4%BA%BA%E5%BD%B1%E8%A7%86.mp4|254846637|a06f0196d8198e23583ae2e65c33ffdd|h=zxwb44ys3awphn75effkc7cxw7dgu5yn|/\">S06E09.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:2db9bbf50947e1b6350641c65fced3954c7ffc66&amp;dn=Baby.Daddy.S06E09.720p.WEB.x264-TBS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E10.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P-%E4%BA%BA%E4%BA%BA%E5%BD%B1%E8%A7%86.mp4|256213332|bf79be1fd1852fcd764d97a8bb6654d8|h=2wmuew3n3mnvsv5fl4o54zenfkewgayj|/\">S06E10.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:752a0e4808d800a4d64629dd92bafe1a9fbc4067&amp;dn=Baby.Daddy.S06E10.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "S06E11.字幕版-HDtv.mp4｜<a href=\"magnet:?xt=urn:btih:831e843eaef77e4e3f7abd4b86e370e9fede0b51&amp;dn=Baby.Daddy.S06E11.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "S06E12.字幕版-HDtv.mp4｜720P<br>\n" +
//                "S06E13.字幕版-HDtv.mp4｜720P<br>\n" +
//                "S06E14.字幕版-HDtv.mp4｜720P<br>\n" +
//                "S06E15.字幕版-HDtv.mp4｜720P<br>\n" +
//                "S06E16.字幕版-HDtv.mp4｜720P<br>\n" +
//                "S06E17.字幕版-HDtv.mp4｜720P<br>\n" +
//                "S06E18.字幕版-HDtv.mp4｜720P<br>\n" +
//                "S06E19.字幕版-HDtv.mp4｜720P<br>\n" +
//                "S06E20.字幕版-HDtv.mp4｜720P</p>\n" +
//                "</div>\n";

//        Pattern pattern = Pattern.compile("<a href=\"http://cn163.net/archives/([^\\\"]+)\">(.*?)</a>");
//        Matcher matcher = pattern.matcher(template);
//        while (matcher.find()) {
//            log.info(matcher.group(1));
//            log.info(matcher.group(2));
//            String sourceUrl = "http://cn163.net/archives/" + matcher.group(1);
//            String url = "http://www.liuxueba.top/";//todo find
//            template = template.replace(matcher.group(0), "<a href=\"" + url + "\">" + matcher.group(2) + "</a>");
//        }
//
//        log.info(template);

        List<SourceArticle> sourceArticleList = sourceArticleRepository.findAll();

        List<AddOnArticle> list = addOnArticleRepository.findAll();

        for (AddOnArticle addOnArticle : list) {
            String body = getNewContent(sourceArticleList, addOnArticle.getBody());
            addOnArticle.setBody(body);
            addOnArticleRepository.save(addOnArticle);
            log.info("do " + addOnArticle.getAid());
        }

        log.info("finish do recommend");

    }

    private String getNewContent(List<SourceArticle> sourceArticleList, String content) {
        Pattern pattern = Pattern.compile("<a href=\"http://cn163.net/archives/([^\\\"]+)\">(.*?)</a>");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String sourceUrl = "http://cn163.net/archives/" + matcher.group(1);
            //找到地址对于的title
            String title = getTitle(sourceArticleList, sourceUrl);
            if (!title.equals("")) {
                //找到新地址
                List<Archives> list = archivesRepository.findByTitle(title);
                if (list != null && list.size() > 0) {
                    String toUrl = "/archives/" + list.get(0).getId() + ".html";
                    content = content.replace(matcher.group(0), "<a href=\"" + toUrl + "\">" + matcher.group(2) + "</a>");
                } else {
                    log.info(sourceUrl + " 地址没有");
                }
            } else {
                log.info(sourceUrl + " 地址没有");
            }
        }
        return content;
    }

    private String getTitle(List<SourceArticle> sourceArticleList, String sourceUrl) {
        for (SourceArticle s : sourceArticleList) {
            if (s.getFromUrl().equals(sourceUrl)) {
                return s.getTitle();
            }
        }
        return "";
    }

    /**
     * 模拟登录
     */
//    @Scheduled(initialDelay = 1000, fixedDelay = 1000 * 3600 * 24 * 365)
    public void login() {
//        String loginUrl = "http://www.liuxueba.top/top100/login.php";
//
//        WebDriver webDriver = webDriverService.webDriverGenerator();
//        webDriver.get(loginUrl);
//        webDriver.findElement(By.name("userid")).sendKeys("admin");
//        webDriver.findElement(By.name("pwd")).sendKeys("admin");
//        String c = "menuitems=1_1%2C2_1%2C3_1%2C4_1%2C5_1; PHPSESSID=n1qjujujatt86ddme3c3o4be25; lastCid=32; lastCid__ckMd5=e497a6783a203112; DedeUserID=1; DedeUserID__ckMd5=4d5b7d516d007fa0; DedeLoginTime=1497584452; DedeLoginTime__ckMd5=1819d52f7fa8e82b";
//        String d = "menuitems=1_1%2C2_1%2C3_1%2C4_1%2C5_1; PHPSESSID=n1qjujujatt86ddme3c3o4be25; lastCid=32; lastCid__ckMd5=e497a6783a203112; DedeUserID=1; DedeUserID__ckMd5=4d5b7d516d007fa0; DedeLoginTime=1497584452; DedeLoginTime__ckMd5=1819d52f7fa8e82b;";
//        String e = "menuitems=1_1%2C2_1%2C3_1%2C4_1%2C5_1; PHPSESSID=n1qjujujatt86ddme3c3o4be25; lastCid=32; lastCid__ckMd5=e497a6783a203112; DedeUserID=1; DedeUserID__ckMd5=4d5b7d516d007fa0; DedeLoginTime=1497596387; DedeLoginTime__ckMd5=665238f8f8c45b28";
//        Map<String, String> map = new HashMap<>();
//        map.put("menuitems", "1_1%2C2_1%2C3_1%2C4_1%2C5_1");
//        map.put("PHPSESSID", "n1qjujujatt86ddme3c3o4be25");
//        map.put("lastCid", "32");
//        map.put("lastCid__ckMd5", "e497a6783a203112");
//        map.put("DedeUserID", "1");
//        map.put("DedeUserID__ckMd5", "4d5b7d516d007fa0");
//        map.put("DedeLoginTime", "1497596387");
//        map.put("DedeLoginTime__ckMd5", "665238f8f8c45b28");


        List<SourceArticle> sourceArticleList = sourceArticleRepository.findAll();

//        String replace = "<script type=\"text/javascript\" language=\"javascript\" src=\"http://cn163.net/ads/300.js\"></script>";
        for (SourceArticle sourceArticle : sourceArticleList) {
            if (sourceArticle.getId() > 658) {
                try {
                    saveArticle(sourceArticle.getTitle(), sourceArticle.getKeywords(), sourceArticle.getTitle()
                            , getTypeId(sourceArticle.getCategory(), sourceArticle.getTitle()));
                } catch (Exception e) {
                    log.info(sourceArticle.getTitle() + " 处理失败");
                }
            }
        }


//        String template = "\n" +
//                "<div id=\"entry\">\n" +
//                "<div class=\"ad_r\">\n" +
//                "<script type=\"text/javascript\" language=\"javascript\" src=\"http://cn163.net/ads/300.js\"></script></div> <p>英文译名Baby Daddy，第4季(2014)ABC.<br>\n" +
//                "本季看点：《少男奶爸》讲述的是二十多的小伙子Ben是个酒保,突然在有一天发现前女友把他们激情后的结晶,一位女婴,留在了他租住的房门外。一位各方面都不成熟的小年轻突然要承担起为人之父的职责。经过一番思想斗争,他决定抚养起这个孩子。育儿故事就此展开。<br>\n" +
//                "-<br>\n" +
//                "网友评论：属于表演和剧情都比较浮夸的家庭笑闹剧，但是演员们都还算得上俊男靓女，尤其是小东西非常可爱。奶爸的喜剧，没有多大亮点，中规中矩，插科打诨，嬉笑怒骂，和《<a href=\"http://cn163.net/archives/1819/\">家有喜旺</a>》是两种感觉，这剧偏搞笑些，尤其是Tucker和Bonnie。</p>\n" +
//                "<p>相关：《<a href=\"http://cn163.net/archives/1904/\">少男奶爸第一季</a>》《<a href=\"http://cn163.net/archives/1906/\">少男奶爸第二三季</a>》<br>\n" +
//                "<img class=\"alignnone size-full wp-image-10433\" src=\"https://farm2.staticflickr.com/1658/25068580270_228fc2e974.jpg\" alt=\"少男奶爸\" original=\"https://farm2.staticflickr.com/1658/25068580270_228fc2e974.jpg\" height=\"405\" width=\"270\"><br>\n" +
//                "播出：ABC Family 类 型：喜剧<br>\n" +
//                "地区：美国 主演：让-卢克·比洛多、切尔西·斯特伯<br>\n" +
//                "语言：英语 首播日期：2012-06-20 周三<br>\n" +
//                "英文：Baby Daddy 别名：少男老爸 第四季<br>\n" +
//                "类似推荐《<a href=\"http://cn163.net/archives/2344/\">查莉成长日记</a>》<br>\n" +
//                "高清美剧全集迅雷下载地址-本季集数：22<br>\n" +
//                "在下面链接上点击或右键选择使用迅雷下载即可<br>\n" +
//                "3种格式MKV、MP4是带双语字幕<br>\n" +
//                "Baby Daddy 第四季<br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E01.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247383064|2352e6ee2b2d6f1fba10aa1f6e42a611|h=4547d5nczd7ecxopeidpf5rrskdjkos7|/\">第01集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E01.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|125137239|5bde20b50caa0d4ce3f8feaf8a1868f1|h=xfqazxmyapk7cycxnjogpkkgkzabmzgw|/\">MP4</a>—<a href=\"ed2k://|file|Baby.Daddy.S04E01.720p.HDTV.x264-KILLERS.mkv|754320824|E32DA02A9D175281653064E045FB7048|h=U7GT534LGFGGJ2KSEGFT2QWFHUD76UQ4|/\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E02.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247021871|95c973b2538d147bca2e92a072ee7540|h=kcnl2hnralluuwe4vxxxxbajmnch7sq3|/\">第02集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E02.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124564573|ee7402254683d1e056eff2e5b364603f|h=7yeltkn2htzs5prd6iznwiegul3tuclq|/\">MP4</a>—<a href=\"magnet:?xt=urn:btih:142172524cf1995bfc3450e494cdae5c7621e528&amp;dn=Baby.Daddy.S04E02.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E03.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247414865|0631736079a35974a7aeea63bf6a26ac|h=abbpw32ghkrgz4wrq36f6nfe2af7sb3m|/\">第03集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E03.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124777557|fd7bc969ac61c9ec424059766890a507|h=hcwc5rngqhh6e7apzau7sxjljtrisph2|/\">MP4</a>—<a href=\"magnet:?xt=urn:btih:fe6013ff24e434b708ca02257872b2af3fd455e4&amp;dn=Baby.Daddy.S04E03.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E04.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|267490120|021a7dfed2755288cc8201ec24ecc132|h=67ujfb3e7hpmkhbeuohh3bgdi6zo7kxt|/\">第04集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E04.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|134887050|f2cf1cda8684c46056c721be0c45f06f|h=lfxkcpvncbr4czvquziwv5rftwi4m5qp|/\">MP4</a>—<a href=\"magnet:?xt=urn:btih:8a0b4ed76ccfcc28f51b0a3b8e636a782714f36b&amp;dn=Baby.Daddy.S04E04.720p.HDTV.x264-IMMERSE%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E05.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247382020|eb4c2c43678f5156799595472b1f95e2|h=axxqfthozkrknps5h3bbrxvwef5o4mgr|/\">第05集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E05.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124743698|afbecb0424057f293928b0e5b0fa7d74|h=qfg2eolebfzvuwbtckmb75yyhoah6bzl|/\">MP4</a>—<a href=\"magnet:?xt=urn:btih:4d590eefd45046ac3c3646cec3c04e23b8e1ac67&amp;dn=Baby.Daddy.S04E05.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E06.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247281129|598dae05acc25314a922a4f159837913|h=c6h32vuq4gzb7adbsxbplwleinwnaw2u|/\">第06集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E06.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124688504|21ec3b3cb33fa0c29c6bfb8447e188ab|h=sxl5ycrfmyab652rv655t725f5gj5x54|/\">MP4</a>—<a href=\"magnet:?xt=urn:btih:22859ba077028782d22234f528265ee60deb8366&amp;dn=Baby.Daddy.S04E06.REAL.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E07.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247370954|af50736c3ae8622e90a25c540c681350|h=z5qfr7ovwutuhcqlvmlp4yk5q5dh5uvt|/\">第07集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E07.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124768086|608c1953ac2cdef81796db51b8fc714a|h=vcukvkugosvzpitoykepziwxelh4s7nb|/\">MP4</a>—<a href=\"magnet:?xt=urn:btih:6a13c5d7ae17f2cc226bb3f92f92ff8bae3ee5f9&amp;dn=Baby.Daddy.S04E07.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E08.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247399316|0c0cc3b3e8aa4458f3daf65ee75b119d|h=bwe3jjdzteffky4hqdninfz3tpn2ns3r|/\">第08集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E08.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124761543|ca949de495002e591f6796022ce70b17|h=nkq5vv4gx56obt45ukrzcfycwoavbgmq|/\">MP4</a>—<a href=\"magnet:?xt=urn:btih:b85e839966c5b69448b1aad61708a225ec1404ef&amp;dn=Baby.Daddy.S04E08.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E09.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247375011|50bdbf7e2c8f1f46a76f34f24b5d9237|h=bmf6jutdagms4ybhxyfseyomr2k56edf|/\">第09集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E09.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124783876|28df2e1e48950811fe0ad9532c782ecf|h=jnlofx5olu534uiwgabzuz4o3bpznd4y|/\">MP4</a>—<a href=\"magnet:?xt=urn:btih:b1d2c98562ea09f48e4ec55e1d627ac5471046e6&amp;dn=Baby.Daddy.S04E09.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E10.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247348272|e91dbd45352b87e53d48b870c86b7dc5|h=vdbx3zv3p5t3amgjmbjjyta4z6adujkn|/\">第10集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E10.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124786569|e762fe4cd4c0ce32a03dca69757a92ee|h=vwkpgmkaceahkfyhb7fgbvk74u5cvxpn|/\">MP4</a>—<a href=\"magnet:?xt=urn:btih:175db6c3ec2babd7aab6f9e4e336f6f7a315dd2f&amp;dn=Baby.Daddy.S04E10.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E11.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|248070490|faf2ea6891c7f6e089f49c3b34ce5f59|h=kotkscsnnj7osbg24e7iulmqwilixdnk|/\">第11集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E11.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|125132399|25a53c6355216cd04379e277887063e9|h=kgmiahciolirscr2mf5fglvhgdj76gei|/\">MP4</a>—<a href=\"magnet:?xt=urn:btih:63c69afe3b13a84d225c84fd8af75687947652b9&amp;dn=Baby.Daddy.S04E11.720p.HDTV.x264-IMMERSE%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E12.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247505202|3d778c4e82327a0220667762c010bcb7|h=yhhgwz5nlvl5ehq227pmpqel5phazw2r|/\">第12集.HD1024.mkv</a>—<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E12.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720X400.mp4|124860561|3c66b45e318630fd6faadd3fce2c7d8e|h=o2ny7mtnfqunzv6f2pskm76lzzvsgevv|/\">MP4</a>—<a href=\"magnet:?xt=urn:btih:d4a9f9ff26e51dc5e32923372214c3a76f324ff3&amp;dn=Baby.Daddy.S04E12.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F11.rarbg.me%3A80&amp;am\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E13.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247338308|eee4e63e75315a2c0f0103bf076f2842|h=vptrh2mq2eodfdg25v3ft6hnvr2nce27|/\">第13集.HD1024.mkv</a>—<a href=\"magnet:?xt=urn:btih:3f9fdf50ccd7b1753fdca2ab71648dea1b0704a9&amp;dn=Baby+Daddy+S04E13+HDTV+x264-ASAP%5Bettv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">MP4</a>—<a href=\"magnet:?xt=urn:btih:2d7b9ebeb051f3b5fcac3629da40a53716dbb592&amp;dn=Baby.Daddy.S04E13.720p.HDTV.x264-IMMERSE%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E14.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247683603|7bd0b699499759ad63431d6f64314532|h=pm63vbjkfdfzk4b44x2r3jkf7v72h7rq|/\">第14集.HD1024.mkv</a>—<a href=\"magnet:?xt=urn:btih:64809173a73cc8a3395ba401d4d82f9c88ebd61b&amp;dn=Baby.Daddy.S04E14.HDTV.x264-ASAP%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">MP4</a>—<a href=\"magnet:?xt=urn:btih:bfa53bdcca3edf2fa124be0d0bc724a72c468dc4&amp;dn=Baby.Daddy.S04E14.720p.HDTV.x264-IMMERSE%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E15.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247442354|29c34a3c141af4ed90c3125eb995a2f4|h=wnb4rbddc6z2fezoxtje73pk4bmd4azf|/\">第15集.HD1024.mkv</a>—<a href=\"magnet:?xt=urn:btih:cec7541868d4a00c4add544ea1254c652ee9f480&amp;dn=Baby.Daddy.S04E15.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">MP4</a>—<a href=\"magnet:?xt=urn:btih:6158109409036b760b2774ce65c3a094602a6053&amp;dn=Baby.Daddy.S04E15.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E16.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247439575|f3c6782bfcc428187bea0c24816ec44a|h=q45oipcsi5zwg53imkia3arrfga5fk7n|/\">第16集.HD1024.mkv</a>—<a href=\"magnet:?xt=urn:btih:c8065dea162a66899b7aea8460f0abd680b13791&amp;dn=Baby.Daddy.S04E16.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">MP4</a>—<a href=\"magnet:?xt=urn:btih:6d3f23d3529153a02bd85eb95b84a57a9d1160b5&amp;dn=Baby.Daddy.S04E16.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E17.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247469488|c7adab53911c94ae8785519d2ed41e19|h=37e3s5bl57jqtxde4ydfruaia32s5qo7|/\">第17集.HD1024.mkv</a>—<a href=\"magnet:?xt=urn:btih:97d17c292f6e10b195ad5ab0842ede63f1bc05b9&amp;dn=Baby.Daddy.S04E17.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">MP4</a>—<a href=\"magnet:?xt=urn:btih:f31ac952600832db64a7781dccd04c37f3a12002&amp;dn=Baby.Daddy.S04E17.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E18.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247241927|21eafc668acd4b423b727711f9811a29|h=silikdj7cypycxabxofpjsabifquza23|/\">第18集.HD1024.mkv</a>—<a href=\"magnet:?xt=urn:btih:5e571b8decbd0f6c9a62d5939bf50bf72289ca65&amp;dn=Baby+Daddy+S04E18+HDTV+x264-KILLERS%5Bettv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">MP4</a>—<a href=\"magnet:?xt=urn:btih:d5a23e11ee603c6c088073f13723603153d3fe97&amp;dn=Baby.Daddy.S04E18.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E19.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247651880|4a01b2bdc12a10fe0ba08769e0c328a3|h=2rdof2hmqw72vvd2eshcl4mgc7vlv4tq|/\">第19集.HD1024.mkv</a>—<a href=\"magnet:?xt=urn:btih:b135f0b96598bf365e4add8938c89afa62f0e21c&amp;dn=Baby.Daddy.S04E19.HDTV.x264-ASAP%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">MP4</a>—<a href=\"magnet:?xt=urn:btih:da1d3c9e15c4899a2589b89c0acce5e639ad6aa3&amp;dn=Baby.Daddy.S04E19.720p.HDTV.x264-IMMERSE%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E20.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247525424|1293afa233ae0a6609def300d8043b0f|h=joswatsjeokce36x26lrgvbrsuzdzlvw|/\">第20集.HD1024.mkv</a>—<a href=\"magnet:?xt=urn:btih:80c43cdf200e540d1253aa1232d3b4ae1efbe285&amp;dn=Baby+Daddy+S04E20+HDTV+x264-KILLERS%5Bettv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">MP4</a>—<a href=\"magnet:?xt=urn:btih:f32eae3aebbb2bc4472a3f513e2247fea559736b&amp;dn=Baby.Daddy.S04E20.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E21.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247502880|0a7b7adfa567d45ddae773045c367150|h=5sj6zzgebxvspajv6gnwjy63z7jnycfs|/\">第21集.HD1024.mkv</a>—<a href=\"magnet:?xt=urn:btih:f758bc4a31c9306c2b0b0695f6f105bee95073d7&amp;dn=Baby.Daddy.S04E21.HDTV.x264-KILLERS%5Bettv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">MP4</a>—<a href=\"magnet:?xt=urn:btih:29b279d9ed37bf70bf37cbbffdf0d827a2c7c0db&amp;dn=Baby.Daddy.S04E21.720p.HDTV.x264-KILLERS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S04E22.End.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HR-HDTV.AC3.1024X576.x264.mkv|247545563|c475debb2495bcb97c555dc0ff00ef87|h=6q6q5435aob3b7wzn547mzbeauideygu|/\">第22集.HD1024.mkv</a>—<a href=\"magnet:?xt=urn:btih:29d5a300ed8fe2c05b46a49ce35188c9e3537801&amp;dn=Baby.Daddy.S04E22.HDTV.x264-ASAP%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">MP4</a>—<a href=\"magnet:?xt=urn:btih:6a768e5ffb7a9d9dec0511de3bd6767c486736c1&amp;dn=Baby.Daddy.S04E22.720p.HDTV.x264-IMMERSE%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<strong>少男老爸第五季,每周四播（本季终）</strong><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E01.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|209727014|7111fb84370664c3b390cfd97e9bffcf|h=serl4xrdjbor2nedup573ysfvyjyxrkp|/\">S05E01.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:d1ec078d486cefc6abe19e54286dd254f101346f&amp;dn=Baby.Daddy.S05E01.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E02.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|211685924|198545727ac9249a969338612fd3660f|h=dtnnaa6zu7ohdqi5ch4ydzz67wt424wu|/\">S05E02.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:2fcbac7760b0f61eb2b995065fe01eca7b29ee9a&amp;dn=Baby.Daddy.S05E02.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E03.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.v2.mp4|213070111|82fe9a64a04379f4f62df945ce5fae67|h=57wvjcarlbgrdt5cz7zxncxrsj7ab7vp|/\">S05E03.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:5367f92c52ccecaa68954efb074de67448bc7b44&amp;dn=Baby.Daddy.S05E03.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E04.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|211253020|4458ad656d080c569e7cacc1679c591f|h=pmrd6isz4pdcowmg3ge3xd2u56vj5mok|/\">S05E04.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:88bac96b288c7149c75c4d788b33b0ce84e582bd&amp;dn=Baby.Daddy.S05E04.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E05.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|211203738|58f80c8242800c582d91e676d5d7694c|h=633wzubmd2uo4ehyn7ebl5vczzhf6tmj|/\">S05E05.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:4757e5fc5930b29db811b67069a4bb1258421870&amp;dn=Baby.Daddy.S05E05.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E06.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|210566227|3586a7d0be734de6455e5e6a82b6f265|h=ehpt2cvtslvevfdinflw7hqzrzy4ylle|/\">S05E06.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:e771fcb928fb43959d6ca0709a5bc58f1a54a4eb&amp;dn=Baby.Daddy.S05E06.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E07.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|212136117|1abe70024eb736bca9c00229e63fb309|h=mfmfmsnaprynobhibqxzxnkjmskhur4n|/\">S05E07.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:bf0c76eb4078dbf398771ca39ae243edce46f756&amp;dn=Baby.Daddy.S05E07.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E08.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|209118734|b40d04939a8208e87bf069d7688f3b69|h=okixj6wh3p2b7dlnaf3i225ca46ob22r|/\">S05E08.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:89a12e2f4b3d6deb8688f71f1c62f2841caa600c&amp;dn=Baby.Daddy.S05E08.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|Baby.Daddy.S05E09.%E5%8F%8C%E8%AF%AD%E5%AD%97%E5%B9%95.720p.UnIon%E5%AD%97%E5%B9%95%E7%BB%84.mkv|375116308|6ecef093f233e5ffa59f7e4e04871963|h=56wxa2pl2t55d72xyriyb5i6nuvaaj75|/\">S05E09.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:a079af315ec4f09dba650a6e38a5e807b5a2c261&amp;dn=Baby.Daddy.S05E09.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|Baby.Daddy.S05E10.%E5%8F%8C%E8%AF%AD%E5%AD%97%E5%B9%95.720p.UnIon%E5%AD%97%E5%B9%95%E7%BB%84.mkv|350882410|168cb96e63363e51fba18dbc4de29953|h=6uzf7cfoxur3lyewobri2ruyekqrkghg|/\">S05E10.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:ed897bd75d84d7aa336600ccbdcaf2a42e772306&amp;dn=Baby.Daddy.S05E10.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E11.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|209820128|363c7de9d615333d4cca5ab0d2269a86|h=qydkiygxplfxkr5ej75ycrjsg2sieicj|/\">S05E11.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:f8bbf1bd86eff8553956786cbf93a0ba4be05e38&amp;dn=Baby.Daddy.S05E11.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E12.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|210424659|8bae6afb99b4f963162a00ce6ea50407|h=usbvk7hnq4e5zloligdm4dbnfl7dy6hn|/\">S05E12.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:8191b5a6995be72978a770e23d24684490366dcd&amp;dn=Baby.Daddy.S05E12.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|Baby.Daddy.S05E13.%E5%8F%8C%E8%AF%AD%E5%AD%97%E5%B9%95.720p.TVrip.x264-UnIon%E5%AD%97%E5%B9%95%E7%BB%84.mkv|482364360|49b8a233fccaf6218223e5c0762a7671|h=5woop6tmuhjj2vafxx25qz3sfcjuewt3|/\">S05E13.字幕版-HDtv.mp4</a>｜<a href=\"ed2k://|file|Baby.Daddy.S05E13.720p.HDTV.x264-AVS.mkv|654326604|9DE9F8EE205A17C858C025ABD78A6B41|h=REEGFPJ56K6HNQYCH2MF6AZXTCEVB7PY|/\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E14.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.V2.mp4|208152370|6452bee03a8b0b4f26b1f25281a1ab74|h=7zr32i5tgltgxejw53np4pprtwaez346|/\">S05E14.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:ffa64c8128dbe1a2d2b1af7470eb508a783f32da&amp;dn=Baby.Daddy.S05E14.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E15.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|206068934|13d70a266419fe1f3728698e4a719f76|h=nrlz7n42ud4qteeljp72ilegvxrgzfbx|/\">S05E15.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:5878aa798926af83910e84319ad59741eb365472&amp;dn=Baby.Daddy.S05E15.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|Baby.Daddy.S05E16.%E5%8F%8C%E8%AF%AD%E5%AD%97%E5%B9%95.720p.TVrip.x264-UnIon%E5%AD%97%E5%B9%95%E7%BB%84.mkv|570758454|77db6464d352c47879dbd5298430bdfb|h=u42yg52lphmxeadzofef6i7znuqau457|/\">S05E16.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:91673c7cee8ca5f62b66de46a1678b838a092a95&amp;dn=Baby.Daddy.S05E16.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E17.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|208053126|94c7e7b07e59bc585f59137be73fbb91|h=owpn6csqnxvuqoxmb33ymqqvgn5upls2|/\">S05E17.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:8607076ec3d333e0a5ea2805fd5b89e0e2a27568&amp;dn=Baby.Daddy.S05E17.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E18.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|205016433|4b8b85cf294011e6dad22e86c9f139c2|h=fw6g6eri6totzk556heafsboqtmwd6bu|/\">S05E18.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:d46c091d5585a7204cf33cd08d8810977412c85e&amp;dn=Baby.Daddy.S05E18.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E19.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|206283218|80c53d9f7d0c1390f6171790ac4d1d29|h=q75vzvnfw5jetjkpok5kfgpo3dbhl5zp|/\">S05E19.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:d8b2243d0841180be82da45974b449ace1cc2622&amp;dn=Baby.Daddy.S05E19.720p.HDTV.x264-AVS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S05E20.END.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.1024X576.mp4|209198388|670ad82b150707548c0ca21fff473886|h=yn73hwoopt7dfi5awgynb3sdk5f6ywle|/\">S05E20.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:6c447e41a3595ff5d28766c000ea60b4a182766e&amp;dn=Baby.Daddy.S05E20.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<strong>少男奶爸第六季</strong>,（2017年3月14回归）<br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E01.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P.mp4|242313211|b9c509dbc21de4faefa45cc2b813841c|h=juo6q7xpoetsebocn3kik7z653nwt4fw|/\">S06E01.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:8a3d8dccbd40f776da91e51b7cd9e527f39fa1a7&amp;dn=Baby.Daddy.S06E01.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E02.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P.mp4|235136203|b0541505a0e008cccea8730b7ae2d6e9|h=zujh3knzregjqhw3sgcg7y4tm4i4ps3z|/\">S06E02.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:3675d201fe28b1f6254d5504c0bb13295d14ede7&amp;dn=Baby.Daddy.S06E02.PROPER.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A271\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E03.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P.mp4|241170735|6cb7545e27ea8c58c56e6206b9e4fde8|h=pzcj6gq6yqxzw2rrq5btpkguv3cma6iz|/\">S06E03.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:67ae2270d81be34b1ed053c8bf0c5b2111e4e0d4&amp;dn=Baby.Daddy.S06E03.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E04.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P.mp4|243132727|64915d005f0a1f6b8e69208d57360d52|h=w6uqu55stxbslr7z5eywqrh2oqkxfv77|/\">S06E04.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:cdd1b1aa48280ea88a9619662661648edf1c97f4&amp;dn=Baby.Daddy.S06E04.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E05.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P.mp4|242841693|7e36b6329c8a68d782a18cbb9b7ae505|h=u2fzocnmluj55m5n5zomnsnaonp5korq|/\">S06E05.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:cdd1b1aa48280ea88a9619662661648edf1c97f4&amp;dn=Baby.Daddy.S06E04.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E06.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P-%E4%BA%BA%E4%BA%BA%E5%BD%B1%E8%A7%86.mp4|243311550|23042663b49932d7ebce650673a408af|h=okea75vvqa5hattaeaqplihzm5mgtu5i|/\">S06E06.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:36dc2b2738a35d33037e9167891ce00f59660360&amp;dn=Baby.Daddy.S06E06.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E07.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P-%E4%BA%BA%E4%BA%BA%E5%BD%B1%E8%A7%86.mp4|244361681|37766d6be63ab249e4053a3d649fa198|h=qyus3qm4inzpeijbo3ii6bzawrl2dc2d|/\">S06E07.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:81e5e9e4ca2aa2ecd65eb8a5ea8769c7bd807515&amp;dn=Baby.Daddy.S06E07.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E08.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P-%E4%BA%BA%E4%BA%BA%E5%BD%B1%E8%A7%86.mp4|275487624|54e234413668739fa7e6ca39ec2e4a93|h=zqjnnn4oa3nahopxwsctk3adi5wuql4i|/\">S06E08.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:a632a68b1f86d38b2e7db60962885a13f777efd0&amp;dn=Baby.Daddy.S06E08.720p.HDTV.x264-FLEET%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E09.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P-%E4%BA%BA%E4%BA%BA%E5%BD%B1%E8%A7%86.mp4|254846637|a06f0196d8198e23583ae2e65c33ffdd|h=zxwb44ys3awphn75effkc7cxw7dgu5yn|/\">S06E09.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:2db9bbf50947e1b6350641c65fced3954c7ffc66&amp;dn=Baby.Daddy.S06E09.720p.WEB.x264-TBS%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "<a href=\"ed2k://|file|%E5%B0%91%E7%94%B7%E5%A5%B6%E7%88%B8.Baby.Daddy.S06E10.%E4%B8%AD%E8%8B%B1%E5%AD%97%E5%B9%95.HDTVrip.720P-%E4%BA%BA%E4%BA%BA%E5%BD%B1%E8%A7%86.mp4|256213332|bf79be1fd1852fcd764d97a8bb6654d8|h=2wmuew3n3mnvsv5fl4o54zenfkewgayj|/\">S06E10.字幕版-HDtv.mp4</a>｜<a href=\"magnet:?xt=urn:btih:752a0e4808d800a4d64629dd92bafe1a9fbc4067&amp;dn=Baby.Daddy.S06E10.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "S06E11.字幕版-HDtv.mp4｜<a href=\"magnet:?xt=urn:btih:831e843eaef77e4e3f7abd4b86e370e9fede0b51&amp;dn=Baby.Daddy.S06E11.720p.HDTV.x264-SVA%5Brartv%5D&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710\">720P</a><br>\n" +
//                "S06E12.字幕版-HDtv.mp4｜720P<br>\n" +
//                "S06E13.字幕版-HDtv.mp4｜720P<br>\n" +
//                "S06E14.字幕版-HDtv.mp4｜720P<br>\n" +
//                "S06E15.字幕版-HDtv.mp4｜720P<br>\n" +
//                "S06E16.字幕版-HDtv.mp4｜720P<br>\n" +
//                "S06E17.字幕版-HDtv.mp4｜720P<br>\n" +
//                "S06E18.字幕版-HDtv.mp4｜720P<br>\n" +
//                "S06E19.字幕版-HDtv.mp4｜720P<br>\n" +
//                "S06E20.字幕版-HDtv.mp4｜720P</p>\n" +
//                "</div>\n";
//        saveArticle(webDriver, "abc", "kw", template.replace(replace, ""), 32);

//        webDriver.quit();

    }


    private Integer getTypeId(String category, String title) {
        Integer typeId = 0;
 /*
<option value="30" class="option3">动作战争</option>
<option value="31" class="option3">科幻魔幻</option>
<option value="32" class="option3">罪案谍战</option>
<option value="33" class="option3">灵异惊悚</option>
<option value="34" class="option3">情景喜剧</option>
<option value="35" class="option3">律政医务</option>
<option value="36" class="option3">真人秀</option>
<option value="37" class="option3">纪录片</option>
<option value="38" class="option3">都市情感</option>
<option value="39" class="option3">动漫卡通</option>
*/
        if (category.contains("动作分类"))
            typeId = 30;
        else if (category.contains("灵异惊悚"))
            typeId = 33;
        else if (category.contains("科幻魔幻"))
            typeId = 31;
        else if (category.contains("罪案谍战"))
            typeId = 32;
        else if (category.contains("情景喜剧"))
            typeId = 34;
        else if (category.contains("律政医务剧情"))
            typeId = 35;
        else if (category.contains("真人秀"))
            typeId = 36;

        if (typeId == 0) {
            typeId = 36;
            log.info(title + " 分类异常 已归属到 " + typeId);
        }
        return typeId;

    }


    private void saveArticle(String title, String keywords, String content, Integer typeId) {

        WebDriver driver = webDriverService.webDriverGenerator();

        //1.先请求下网站
        driver.get("http://www.liuxueba.top");

        //2.模拟cookie登录 采用之前登录的cookie
        Map<String, String> map = new HashMap<>();
        map.put("menuitems", "1_1,2_1,3_1");
        map.put("PHPSESSID", "fo45icjei0qsktajnci5lvrc34");
        map.put("DedeUserID", "1");
        map.put("DedeUserID__ckMd5", "4d5b7d516d007fa0");
        map.put("DedeLoginTime", "1497593246");
        map.put("DedeLoginTime__ckMd5", "861246ddf1e114b4");

        String domain = "www.liuxueba.top";
        map.forEach((x, y) -> {
            BasicClientCookie cookie = new BasicClientCookie(x, y);
            cookie.setDomain(domain);
            cookie.setPath("/");
            utilsService.addAloneCookie(cookie, driver);
        });

        //3.添加操作
        String addUrl = "http://www.liuxueba.top/top100/article_add.php";
        driver.get(addUrl);
        driver.manage().window().maximize();
        driver.findElement(By.id("title")).sendKeys(title);

        driver.findElement(By.id("keywords")).sendKeys(keywords);
//选择框
        Select select = new Select(driver.findElement(By.id("typeid")));
        select.selectByValue(typeId.toString());


        //切换到源码 内容过多无法设置数据
//        webDriver.findElement(By.id("cke_8_label")).click();
//        webDriver.findElement(By.className("cke_enable_context_menu")).sendKeys(content);
//        webDriver.findElement(By.id("cke_8_label")).click();

//        WebDriverUtil.waitFor(webDriver, new java.util.function.Predicate<WebDriver>() {
//            @Override
//            public boolean test(WebDriver webDriver) {
//                webDriver.switchTo().frame(1);
//                webDriver.findElement(By.tagName("body")).sendKeys(content);
//                return true;
//            }
//        },10);

        //切换到iframe 富文本框 CKEDITOR.instances.body.insertHtml(picHTML);

        //切回主文档
//        webDriver.switchTo().defaultContent();


        driver.findElement(By.name("imageField")).click();
        driver.quit();
    }

//    public void saveEdit() {
//
//        WebDriver driver = webDriverService.webDriverGenerator();
//
//
//
////        String t = "4050 5316 6479 6864 6901 7139 7148 7476 8155 8293 8862 8956 9008 9051 10549";
//        //19170  19595
//        for (Integer i = 19884; i <= 20010; i++) {
////        for (String x : t.split(" ")) {
////            int i = Integer.parseInt(x);
//            try {
//                editOneOper(i, driver);
//                log.info(i + " do ");
//            } catch (Exception e) {
//                log.info(i + " error");
//            }
//        }
//
//        driver.quit();
//    }


    /**
     * 处理缩略图
     */
//    @Scheduled(initialDelay = 3000, fixedDelay = 1000 * 3600 * 24 * 365)
    public void doLitPic() {

        List<Archives> archives = archivesRepository.findByLitPic();
        for (Archives archives1 : archives) {
            try {
                AddOnArticle addOnArticle = addOnArticleRepository.findOne(archives1.getId());

                String content = addOnArticle.getBody();

                String remotePictureUrl = utilsService.getPicture(content);

                log.info(remotePictureUrl);

                //下载图片 并保存为220X150
                String localPictureUrl = StaticResourceService.template + "/" + UUID.randomUUID().toString().replace("-", "") + remotePictureUrl.substring(remotePictureUrl.lastIndexOf("."));
                download(remotePictureUrl, localPictureUrl);

                String saveUrl = "/uploads/allimg/170617/" + UUID.randomUUID().toString().replace("-", "") + ".jpg";

                File path = new File(commonConfigService.getResourcesHome() + "/uploads/allimg/170617/");
                if (!path.exists()) path.mkdirs();
                Thumbnails.of(commonConfigService.getResourcesHome() + localPictureUrl).sourceRegion(Positions.TOP_CENTER, 220, 150).size(220, 150).outputFormat("jpg").outputQuality(0.8).toFile(commonConfigService.getResourcesHome() + saveUrl);

                if (StringUtils.isEmpty(archives1.getLitPic())) {
                    archives1.setLitPic(saveUrl);
                    archivesRepository.save(archives1);
                }
                log.info("do " + archives1.getId());
            } catch (Exception er) {
                log.info(archives1.getTitle());
            }
        }
        log.info("finished");
    }


    @Autowired
    private StaticResourceService staticResourceService;

    @Autowired
    private CommonConfigService commonConfigService;

    public void download(String urlString, String fileName) throws Exception {
        // 构造URL
        URL url = new URL(urlString);
        // 打开连接
        URLConnection con = url.openConnection();
        //设置请求超时为5s
        con.setConnectTimeout(5 * 1000);
        // 输入流
        InputStream is = con.getInputStream();
        staticResourceService.uploadResource(fileName, is);
    }


    //    @Scheduled(initialDelay = 3000, fixedDelay = 1000 * 3600 * 24 * 365)
    public void doUpdateTime() {
        List<Archives> archives = archivesRepository.findAll();
        for (Archives archives1 : archives) {
            try {

                AddOnArticle addOnArticle = addOnArticleRepository.findOne(archives1.getId());
                String content = addOnArticle.getBody();

                Long updateTime = getUpdateTime(content);

                if (updateTime > 0) {
                    archives1.setUpdateTime(updateTime);
                    archivesRepository.save(archives1);
                }
            } catch (Exception e) {
                log.info(archives1.getTitle() + " do updatetime error");
            }
        }
        log.info("do updatetime finished");
    }

    private Long getUpdateTime(String text) throws ParseException {
        String result = findDate(text, "首播日期");

        if (StringUtils.isEmpty(result)) {
            result = findDate(text, "回归播出时间");
        }

        if (StringUtils.isEmpty(result)) {
            result = findDate(text, "首播");
        }

        if (StringUtils.isEmpty(result)) {
            result = findDate(text, "回归日期");
        }

        if (StringUtils.isEmpty(result)) {
            result = findDate(text, "开播时间");
        }

        result = result.replace("(", "").replace(")", "");

        result = result.length() >= 10 ? result.substring(0, 10) : result;
        if (result.length() == 4) result = result + "-03-06";

        log.info(result);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse(result);
        Long time = date.getTime() / 1000;
        return time;
    }

    private String findDate(String text, String strDateName) {
        String result = "";
        Pattern pattern = Pattern.compile(strDateName + "(.*?)<br");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            result = matcher.group(1).replace("：", "").replace(":", "").replaceAll("\\<.*?>", "");
        }
        return result;
    }

    public void getScore() throws IOException {

        Map<String, Integer> list = new HashMap<>();
        list.put("美国", 40);
        list.put("英国", 15);
        list.put("法国", 1);
        list.put("加拿大", 1);
        list.put("西班牙", 1);
        list.put("意大利", 1);
        list.put("德国", 1);
        list.put("俄罗斯", 1);

        Iterator<Map.Entry<String, Integer>> iterator = list.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> object = iterator.next();
            int n = 0;
            while (n < object.getValue()) {
                String webUrl = "http://www.zimuzu.tv/fresourcelist?page=" + n + "&channel=tv&area=" + object.getKey() + "&category=&year=&tvstation=&sort=score";
                getScoreByUrl(webUrl);
                n += 1;
            }
        }
    }


    private void getScoreByUrl(String webUrl) throws IOException {
        List<SourceScore> sourceScores = new ArrayList<>();

        URL url = new URL(webUrl);
        Source source = new Source(url);
        // List<Element> elements = source.getAllElements();
        Element element = source.getFirstElementByClass("resource-showlist");
        if (element != null) {
            for (Element element1 : element.getChildElements()) {
                for (Element element2 : element1.getChildElements()) {
                    SourceScore sourceScore = new SourceScore();

                    String text = element2.getContent().toString();
                    //<span class="point"><em>9.</em>7</span>
                    BigDecimal score = new BigDecimal(element2.getFirstElementByClass("point").getContent().toString().replace("<em>", "").replace("</em>", ""));
                    //<strong class="tag tv">1</strong>【ff】《11》(Narcos)2015</a><font class="f4">[22]</font>
                    String title = getTitle(element2.getFirstElementByClass("f14").getTextExtractor().toString());
                    String title_en = getTitleEnglish(element2.getFirstElementByClass("f14").getTextExtractor().toString());
                    if (score.compareTo(new BigDecimal("8.5")) >= 0) {
                        sourceScore.setTitle(title);
                        sourceScore.setScore(score);
                        sourceScore.setEnglishTitle(title_en);
                        sourceScores.add(sourceScore);
                    }
                }
            }
        }
        sourceScoreRepository.save(sourceScores);

    }

    private String getTitle(String text) {
        String result = "";
        Pattern pattern = Pattern.compile("《(.*?)》");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            return matcher.group(1);
        }
        return result;
    }

    private String getTitleEnglish(String text) {
        String result = "";
        Pattern pattern = Pattern.compile(".+?\\((.*?)\\)");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            return matcher.group(1);
        }
        return result;
    }


    //@Scheduled(initialDelay = 3000, fixedDelay = 1000 * 3600 * 24 * 365)
    public void updateMovieContentTitle() {
        int id = 19596;
        while (id <= 19781) {
            try {
                Archives archives = archivesRepository.findOne(id);
                if (archives != null && !StringUtils.isEmpty(archives.getTitle())) {
                    SourceArticleFour sourceArticleThree = sourceArticleFourRepository.findByTitle(archives.getTitle());
                    if (sourceArticleThree != null) {
                        AddOnArticle addOnArticle = addOnArticleRepository.findOne(id);
                        if (addOnArticle != null) {
                            addOnArticle.setPingfen(sourceArticleThree.getPingfen());
                            addOnArticle.setBody(sourceArticleThree.getContent());
                            addOnArticleRepository.save(addOnArticle);
                            log.info(id);
                        }
                    }
                }
            } catch (Exception e) {
                log.error(id + " error");
            }
            id++;
        }

    }

    //@Scheduled(initialDelay = 3000, fixedDelay = 1000 * 3600 * 24 * 365)
    public void updateKeywordsNull() {
        List<SourceArticleThree> list1 = sourceArticleThreeRepository.findByKeyworsNull();
        for (SourceArticleThree three : list1) {
            List<Archives> archives = archivesRepository.findByTitle(three.getTitle() + "迅雷下载");
            if (archives != null && archives.size() > 0) {
                for (Archives archives1 : archives) {
                    archives1.setKeywords("其他");
                    archivesRepository.save(archives1);
                    log.info(archives1.getId() + " do");
                }
            }
        }

        List<SourceArticleFour> list2 = sourceArticleFourRepository.findByKeyworsNull();
        for (SourceArticleFour four : list2) {
            List<Archives> archives = archivesRepository.findByTitle(four.getTitle());
            if (archives != null && archives.size() > 0) {
                for (Archives archives1 : archives) {
                    archives1.setKeywords("其他");
                    archivesRepository.save(archives1);
                    log.info(archives1.getId() + " od");
                }
            }
        }
        log.info("finishe");
    }

    /***
     * 原名：King Arthur: Legend of the Sword<br />
     地区：美国<br />
     语 言：英语<br />
     首播：2017-05-12<br />
     制作公司：<br />
     类型：动作/冒险<br />
     IMDB：<a href="http://www.imdb.com/title/tt1972591" onclick="window.open(this.href, '', 'resizable=no,status=no,location=no,toolbar=no,menubar=no,fullscreen=no,scrollbars=no,dependent=no'); return false;">http://www.imdb.com/title/tt1972591</a> 7.2分 60,080票<br />
     别名：亚瑟王：圣剑传奇/亚瑟：王者之剑(台)/神剑亚瑟王(港)/亚瑟王：圆桌骑士/亚瑟王：剑之传奇/亚瑟王：石中剑传说/新亚瑟王/圆桌骑士/King Arthur<br />
     編劇：卓比&middot;哈罗德 / 盖&middot;里奇 / 莱昂内尔&middot;威格拉姆 / 大卫&middot;道金<br />
     导演：盖&middot;里奇<br />
     主演：
     */
    //@Scheduled(initialDelay = 3000, fixedDelay = 1000 * 3600 * 24 * 365)
    public void initSearchItem() {
        //19782 动漫19596-19781 纪录片19170-19595 电影下载 1950-10560 最初美剧<=1949
        List<AddOnArticle> addOnArticles = addOnArticleRepository.findById(0, 1949);
        for (AddOnArticle addOnArticle : addOnArticles) {
            String body = addOnArticle.getBody().replace("<div>", "").replace("</div>", "<br />").replace("<p>", "").replace("</p>", "<br />");
            String englishName = utilsService.findBRSearchItem(body, "原名：");
            if (StringUtils.isEmpty(englishName)) {
                englishName = utilsService.findBRSearchItem(body, "英文：");
            }
            if (StringUtils.isEmpty(englishName)) {
                englishName = utilsService.findBRSearchItem(body, "原名:");
            }
            if (StringUtils.isEmpty(englishName)) {
                englishName = utilsService.findBRSearchItem(body, "英文:");
            }
            if (StringUtils.isEmpty(englishName)) {
                englishName = utilsService.findBRSearchItem(body, "英文名：");
            }
            if (StringUtils.isEmpty(englishName)) {
                englishName = utilsService.findBRSearchItem(body, "英文名:");
            }

            if (!StringUtils.isEmpty(englishName)) {
                if (englishName.indexOf("主演") > 0) englishName = englishName.substring(0, englishName.indexOf("主演"));
                if (englishName.indexOf("相关") > 0) englishName = englishName.substring(0, englishName.indexOf("相关"));
                if (englishName.indexOf("类似推荐") > 0)
                    englishName = englishName.substring(0, englishName.indexOf("类似推荐"));
                if (englishName.indexOf("同类推荐") > 0)
                    englishName = englishName.substring(0, englishName.indexOf("同类推荐"));
                if (englishName.indexOf("又名") > 0) englishName = englishName.substring(0, englishName.indexOf("又名"));
                if (englishName.indexOf("导演") > 0) englishName = englishName.substring(0, englishName.indexOf("导演"));
                if (englishName.indexOf("别名") > 0) englishName = englishName.substring(0, englishName.indexOf("别名"));
                if (englishName.indexOf("，") > 0) englishName = englishName.substring(0, englishName.indexOf("，"));

                if (englishName.indexOf("、") > 0) englishName = englishName.substring(0, englishName.indexOf("、"));
                if (englishName.indexOf("别") > 0) englishName = englishName.substring(0, englishName.indexOf("别"));
                if (englishName.indexOf("第") > 0) englishName = englishName.substring(0, englishName.indexOf("第"));
                if (englishName.indexOf("制") > 0) englishName = englishName.substring(0, englishName.indexOf("制"));
                if (englishName.indexOf("单") > 0) englishName = englishName.substring(0, englishName.indexOf("单"));
                if (englishName.indexOf("更") > 0) englishName = englishName.substring(0, englishName.indexOf("更"));
                if (englishName.indexOf("本") > 0) englishName = englishName.substring(0, englishName.indexOf("本"));
                if (englishName.indexOf("幸") > 0) englishName = englishName.substring(0, englishName.indexOf("幸"));
                if (englishName.indexOf("本") > 0) englishName = englishName.substring(0, englishName.indexOf("本"));

                addOnArticle.setEnglishName(englishName.replace("Season 1", "").replace("Season 2", "").replace("Season 3", "").replace("Season 4", "").replace("Season 5", "")
                        .replace("Season 6", "").replace("Season 7", "").replace("Season 8", "").replace("Season 9", "").trim());
            }

            String areaName = utilsService.findBRSearchItem(body, "地区：");
            if (StringUtils.isEmpty(areaName)) {
                areaName = utilsService.findBRSearchItem(body, "地区:");
            }
            if (StringUtils.isEmpty(areaName)) {
                areaName = utilsService.findBRSearchItem(body, "制片国家/地区：");
            }
            if (StringUtils.isEmpty(areaName)) {
                areaName = utilsService.findBRSearchItem(body, "制片国家/地区:");
            }


            if (!StringUtils.isEmpty(areaName) && !areaName.endsWith("<br")) {
                if (areaName.indexOf("制作公司") >= 0) areaName = areaName.substring(0, areaName.indexOf("制作公司"));
                if (areaName.indexOf("主演") >= 0) areaName = areaName.substring(0, areaName.indexOf("主演"));
                if (areaName.indexOf("导演") >= 0) areaName = areaName.substring(0, areaName.indexOf("导演"));
                areaName = areaName.trim();
                if (areaName.indexOf(" ") >= 0) areaName = areaName.substring(0, areaName.indexOf(" "));
            }
            if (StringUtils.isEmpty(areaName))
                addOnArticle.setAreaName("其他");
            else
                addOnArticle.setAreaName(areaName.trim());


            String language = utilsService.findBRSearchItem(body, "语 言：");
            if (StringUtils.isEmpty(language)) {
                language = utilsService.findBRSearchItem(body, "语言：");
            }
            if (StringUtils.isEmpty(language)) {
                language = utilsService.findBRSearchItem(body, "语言:");
            }
            if (StringUtils.isEmpty(language)) {
                language = utilsService.findBRSearchItem(body, "语 言:");
            }
//            if (StringUtils.isEmpty(language)) {
//                language = findBlankSearchItem(body, "语言：");
//            }
//            if (StringUtils.isEmpty(language)) {
//                language = findBlankSearchItem(body, "语 言：");
//            }


            if (!StringUtils.isEmpty(language) && !language.endsWith("<br")) {
                if (language.indexOf("什么时候") >= 0) language = language.substring(0, language.indexOf("什么时候"));
                if (language.indexOf("什么时候播出日期") >= 0) language = language.substring(0, language.indexOf("什么时候播出日期"));
                if (language.indexOf("首播日期") >= 0) language = language.substring(0, language.indexOf("首播日期"));
                if (language.indexOf("首播") >= 0) language = language.substring(0, language.indexOf("首播"));
                if (language.indexOf("回归日期") >= 0) language = language.substring(0, language.indexOf("回归日期"));
                if (language.indexOf("预计回归") >= 0) language = language.substring(0, language.indexOf("预计回归"));
                if (language.indexOf("回归时间") >= 0) language = language.substring(0, language.indexOf("回归时间"));
                if (language.indexOf("主演") >= 0) language = language.substring(0, language.indexOf("主演"));
                language = language.trim();
                if (language.indexOf(" ") >= 0) language = language.substring(0, language.indexOf(" "));
                addOnArticle.setLanguage(language.trim());
            }
            if (StringUtils.isEmpty(language)) {
                if (!StringUtils.isEmpty(addOnArticle.getAreaName())
                        && (addOnArticle.getAreaName().equals("美国") || addOnArticle.getAreaName().equals("英国"))) {
                    addOnArticle.setLanguage("英语");
                }
            }

            String beginTime = utilsService.findBRSearchItem(body, "首播：");
            if (StringUtils.isEmpty(beginTime)) {
                beginTime = utilsService.findBRSearchItem(body, "首播:");
            }
            if (StringUtils.isEmpty(beginTime)) {
                beginTime = utilsService.findBRSearchItem(body, "首播日期：");
            }
            if (StringUtils.isEmpty(beginTime)) {
                beginTime = utilsService.findBRSearchItem(body, "首播日期:");
            }
            if (StringUtils.isEmpty(beginTime)) {
                beginTime = utilsService.findBRSearchItem(body, "播出日期：");
            }
            if (StringUtils.isEmpty(beginTime)) {
                beginTime = utilsService.findBRSearchItem(body, "播出日期:");
            }
            if (StringUtils.isEmpty(beginTime)) {
                beginTime = utilsService.findBRSearchItem(body, "回归日期：");
            }
            if (StringUtils.isEmpty(beginTime)) {
                beginTime = utilsService.findBRSearchItem(body, "回归日期:");
            }
            if (StringUtils.isEmpty(beginTime)) {
                beginTime = utilsService.findBRSearchItem(body, "预计回归：");
            }
            if (StringUtils.isEmpty(beginTime)) {
                beginTime = utilsService.findBRSearchItem(body, "预计回归:");
            }
            if (StringUtils.isEmpty(beginTime)) {
                beginTime = utilsService.findBRSearchItem(body, "回归时间：");
            }
            if (StringUtils.isEmpty(beginTime)) {
                beginTime = utilsService.findBRSearchItem(body, "回归时间:");
            }


            if (!StringUtils.isEmpty(beginTime)) addOnArticle.setBeginTime(beginTime.trim());

            String corp = utilsService.findBRSearchItem(body, "制作公司：");
            if (StringUtils.isEmpty(corp)) {
                corp = utilsService.findBRSearchItem(body, "制作公司:");
            }
            if (StringUtils.isEmpty(corp)) {
                corp = utilsService.findBRSearchItem(body, "播出：");
            }
            if (StringUtils.isEmpty(corp)) {
                corp = utilsService.findBRSearchItem(body, "播出:");
            }
            if (StringUtils.isEmpty(corp)) {
                corp = utilsService.findBRSearchItem(body, "电视台：");
            }
            if (StringUtils.isEmpty(corp)) {
                corp = utilsService.findBRSearchItem(body, "电视台:");
            }
//            if (StringUtils.isEmpty(corp)) {
//                corp = findBlankSearchItem(body, "播出：");
//            }
            if (!StringUtils.isEmpty(corp)) {
                if (corp.indexOf("类 型") >= 0) corp = corp.substring(0, corp.indexOf("类 型"));

                addOnArticle.setCorp(corp.trim());
            }

            String type = utilsService.findBRSearchItem(body, "类型：");
            if (StringUtils.isEmpty(type)) {
                type = utilsService.findBRSearchItem(body, "类型:");
            }
            if (StringUtils.isEmpty(type)) {
                type = utilsService.findBRSearchItem(body, "类 型：");
            }
            if (StringUtils.isEmpty(type)) {
                type = utilsService.findBRSearchItem(body, "类 型:");
            }
            if (!StringUtils.isEmpty(type)) {
                if (type.indexOf("播出") >= 0) type = type.substring(0, type.indexOf("播出"));
                addOnArticle.setType(type.trim());
            }

            String IMDB = utilsService.findBRSearchItem(body, "IMDB：");
//            if (StringUtils.isEmpty(IMDB)) {
//                IMDB = utilsService.findBRSearchItem(body, "IMDB：");
//            }
            if (!StringUtils.isEmpty(IMDB)) {
                String link = findLink(IMDB);
                if (link.endsWith("/")) link = link.substring(0, link.length() - 1);
                if (link.split("/").length > 0) {
                    IMDB = link.split("/")[link.split("/").length - 1];
                    addOnArticle.setImdb(IMDB.trim());
                }
            }

            String alias = utilsService.findBRSearchItem(body, "别名：");
            if (StringUtils.isEmpty(alias)) {
                alias = utilsService.findBRSearchItem(body, "别名:");
            }
            if (StringUtils.isEmpty(alias)) {
                alias = utilsService.findBRSearchItem(body, "又名：");
            }
            if (StringUtils.isEmpty(alias)) {
                alias = utilsService.findBRSearchItem(body, "又名:");
            }
            if (!StringUtils.isEmpty(alias)) {
                if (alias.indexOf("主演") >= 0) alias = alias.substring(0, alias.indexOf("主演"));
                addOnArticle.setAlias(alias.trim());
            }

            String author = utilsService.findBRSearchItem(body, "編劇：");
            if (body.indexOf("編劇：") < 0 && StringUtils.isEmpty(author)) {
                author = utilsService.findBRSearchItem(body, "编剧：");
            }
            if (!StringUtils.isEmpty(author)) addOnArticle.setAuthor(author.trim());

            String director = utilsService.findBRSearchItem(body, "导演：");
            if (StringUtils.isEmpty(director)) {
                director = utilsService.findBRSearchItem(body, "导演:");
            }
            if (!StringUtils.isEmpty(director)) addOnArticle.setDirector(director.trim());

            String zhuyan = utilsService.findBRSearchItem(body, "主演：");
            if (StringUtils.isEmpty(zhuyan)) {
                zhuyan = utilsService.findBRSearchItem(body, "主演:");
            }
            if (StringUtils.isEmpty(zhuyan)) {
                zhuyan = utilsService.findPSearchItem(body, "主演：");
            }
            if (StringUtils.isEmpty(zhuyan)) {
                zhuyan = utilsService.findPSearchItem(body, "主演:");
            }

            if (!StringUtils.isEmpty(zhuyan)) addOnArticle.setZhuYan(zhuyan.trim());


            Archives archives = archivesRepository.findOne(addOnArticle.getAid());
            if (archives != null) {
                String movieName = archives.getTitle().replace("迅雷下载", "").replace(englishName, "").replace("/全集", "").replace("全集", "");
                movieName = movieName.length() > 100 ? movieName.substring(0, 100) : movieName;
                movieName = movieName.replace("[美剧]", "").replace("[英]", "").replace("[真人季]", "").replace("[英剧]", "").trim();
                if (movieName.indexOf("季") >= 0) {
                    movieName = movieName.substring(0, movieName.lastIndexOf("季") + 1);
                }
                if (!StringUtils.isEmpty(movieName)) addOnArticle.setMovieName(movieName);
            }

            try {
                addOnArticleRepository.save(addOnArticle);
            } catch (Exception ex) {
                log.error(addOnArticle.getAid());
            }
            log.info("do " + addOnArticle.getAid());
        }
        log.info("finished");
    }


//    private String findDivSearchItem(String text, String startTitleName)
//    {
//        String result = "";
//        Pattern pattern = Pattern.compile(startTitleName + "(.*?)</div>", Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher(text);
//        while (matcher.find()) {
//            result = matcher.group(1);
//            break;
//        }
//        return result;
//    }


//    private String findLineBRSearchItem(String text, String startTitleName) {
//        String result = "";
//        Pattern pattern = Pattern.compile(startTitleName + "(.*?)</br", Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher(text);
//        while (matcher.find()) {
//            result = matcher.group(1);
//            break;
//        }
//        return result;
//    }

    private String findBlankSearchItem(String text, String startTitleName) {
        String result = "";
        Pattern pattern = Pattern.compile(startTitleName + "(.*?) ");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            result = matcher.group(1);
            break;
        }
        return result;
    }

    private String findLink(String text) {
        String result = "";
        Pattern pattern = Pattern.compile("<a(.*?)href=\"([^\\\"]+)\"(.*?)>(.*?)</a>");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            result = matcher.group(2);
            break;
        }
        return result;
    }

    private List<String> findLinks(String text) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("<a(.*?)href=\"([^\\\"]+)\"(.*?)>(.*?)</a>");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            if (!matcher.group(4).equals("网盘")) result.add(matcher.group(2));
        }
        return result;
    }


    /**
     * 更新简介和下载地址
     */
    //@Scheduled(initialDelay = 3000, fixedDelay = 1000 * 3600 * 24 * 365)
    public void updateSummaryAnddownUrlsAndRecommonder() {
        List<Integer> types = new ArrayList<>();
        List<AddOnArticle> list;
        types.add(44);
        types.add(37);
        types.add(39);
        list = addOnArticleRepository.findByTypes(types);
        for (AddOnArticle addOnArticle : list) {
            String body = addOnArticle.getBody();
            body = utilsService.clearFormat(body);
            body = body.replace("<p>", "").replace("</p>", "<br />").replace("<div>", "").replace("</div>", "<br />");
            if (body.indexOf("<img") >= 0) {
                addOnArticle.setSummary(body.substring(0, body.indexOf("<img")));
            }
            if (body.indexOf("<h3>") >= 0) {
                addOnArticle.setDownUrls(body.substring(body.indexOf("<h3>")));
            } else if (body.indexOf("<h2>") >= 0) {
                addOnArticle.setDownUrls(body.substring(body.indexOf("<h2>")));
            }
            addOnArticleRepository.save(addOnArticle);
        }
        log.info("do finished 1");


        types = new ArrayList<>();
        types.add(30);
        types.add(31);
        types.add(32);
        types.add(33);
        types.add(34);
        types.add(35);
        types.add(36);
        types.add(42);
        types.add(43);
        list = addOnArticleRepository.findByTypes(types);

        for (AddOnArticle addOnArticle : list) {
            String body = addOnArticle.getBody();
            body = utilsService.clearFormat(body);
            body = body.replace("<p>", "").replace("</p>", "<br />").replace("<div>", "").replace("</div>", "<br />");
            body = body.replace("<br>", "<br />").replace("<br >", "<br />");
            if (body.indexOf("<img") >= 0) {

                String summary = body.substring(0, body.indexOf("<img"));
                summary = summary.trim();
                summary = removeBr(summary);
                addOnArticle.setSummary(summary);
            }

            String downUrls = "";
            if (body.indexOf("<h3") >= 0) {
                downUrls = body.substring(body.indexOf("<h3"));
            } else if (body.indexOf("<h2") >= 0) {
                downUrls = body.substring(body.indexOf("<h2"));
            } else if (body.indexOf("在下面链接上点击右键选择使用迅雷下载即可") >= 0) {
                downUrls = body.substring(body.indexOf("在下面链接上点击右键选择使用迅雷下载即可") + 20);
            } else if (body.indexOf("在下面链接上点击或右键选择使用迅雷下载即可") >= 0) {
                downUrls = body.substring(body.indexOf("在下面链接上点击或右键选择使用迅雷下载即可") + 21);
            }

            downUrls = downUrls.trim();
            downUrls = removeBr(downUrls);
            addOnArticle.setDownUrls(downUrls);

            String recommend = "";
            String findRecommend = utilsService.findBRSearchItem(body, "类似推荐《");
            if (!StringUtils.isEmpty(findRecommend))
                recommend += "《" + findRecommend;
            findRecommend = utilsService.findBRSearchItem(body, "类似推荐：《");
            if (!StringUtils.isEmpty(findRecommend))
                recommend += "《" + findRecommend;
            findRecommend = utilsService.findBRSearchItem(body, "相关下载《");
            if (!StringUtils.isEmpty(findRecommend))
                recommend += "《" + findRecommend;
            findRecommend = utilsService.findBRSearchItem(body, "相关下载：《");
            if (!StringUtils.isEmpty(findRecommend))
                recommend += "《" + findRecommend;
            findRecommend = utilsService.findBRSearchItem(body, "相关推荐《");
            if (!StringUtils.isEmpty(findRecommend))
                recommend += "《" + findRecommend;
            findRecommend = utilsService.findBRSearchItem(body, "相关推荐：《");
            if (!StringUtils.isEmpty(findRecommend))
                recommend += "《" + findRecommend;
            findRecommend = utilsService.findBRSearchItem(body, "相关《");
            if (!StringUtils.isEmpty(findRecommend))
                recommend += "《" + findRecommend;
            findRecommend = utilsService.findBRSearchItem(body, "相关：《");
            if (!StringUtils.isEmpty(findRecommend))
                recommend += "《" + findRecommend;
            if (recommend.indexOf("，") >= 0) recommend = recommend.substring(0, recommend.indexOf("，"));

            addOnArticle.setRecommend(recommend);

            addOnArticleRepository.save(addOnArticle);
        }
        log.info("do finished 2");
    }


    private String removeBr(String body) {
        body = body.trim();
        if (body.startsWith("<br />")) {
            body = body.substring(6);
        }
        if (body.endsWith("<br />")) {
            body = body.substring(0, body.length() - 6);
        }
        return body;
    }


    //@Scheduled(initialDelay = 3000, fixedDelay = 1000 * 3600 * 24 * 365)
    public void findRepeatLink() {
//        WebDriver webDriver = getZMZWebDriver();

        List<Integer> ids = new ArrayList<>();
        //ids.add(19200);ids.add(19208);ids.add(19230);ids.add(19237);ids.add(19247);ids.add(19273);ids.add(19303);ids.add(19338);ids.add(19369);ids.add(19371);ids.add(19407);ids.add(19411);ids.add(19412);ids.add(19418);ids.add(19421);ids.add(19449);ids.add(19452);ids.add(19468);ids.add(19509);ids.add(19542);ids.add(19561);ids.add(19564);ids.add(19567);ids.add(19570);ids.add(19574);ids.add(19608);ids.add(19621);ids.add(19634);ids.add(19659);ids.add(19668);ids.add(19676);ids.add(19689);ids.add(19693);ids.add(19695);ids.add(19697);ids.add(19707);ids.add(19710);ids.add(19720);ids.add(19726);ids.add(19747);ids.add(19753);ids.add(19756);
        //ids.add(4211);ids.add(4240);ids.add(4255);ids.add(4298);ids.add(4348);ids.add(4489);ids.add(4500);ids.add(4574);ids.add(4775);ids.add(4781);ids.add(4889);ids.add(4897);ids.add(4919);ids.add(4928);ids.add(4930);ids.add(4956);ids.add(5004);ids.add(5083);ids.add(5120);ids.add(5354);ids.add(5416);ids.add(5449);ids.add(5489);ids.add(5519);ids.add(5656);ids.add(5671);ids.add(5674);ids.add(5724);ids.add(5869);ids.add(5917);ids.add(5928);ids.add(5996);ids.add(6119);ids.add(6326);ids.add(6432);ids.add(6495);ids.add(6595);ids.add(6780);ids.add(6838);ids.add(6926);ids.add(6939);ids.add(6955);ids.add(7139);ids.add(7143);ids.add(7182);ids.add(7190);ids.add(7420);ids.add(7458);ids.add(7480);ids.add(7519);ids.add(7548);ids.add(7640);ids.add(7821);ids.add(7857);ids.add(7925);ids.add(8092);ids.add(8113);ids.add(8168);ids.add(8205);ids.add(8227);ids.add(8304);ids.add(8311);ids.add(8332);ids.add(8359);ids.add(8432);ids.add(8454);ids.add(8512);ids.add(8529);ids.add(8531);ids.add(8960);ids.add(8963);ids.add(8980);ids.add(9332);ids.add(9416);ids.add(9646);ids.add(9659);ids.add(9677);ids.add(9796);ids.add(9823);ids.add(9987);ids.add(10010);ids.add(10013);ids.add(10084);
        //电影repeat
//        ids.add(37);
//        ids.add(39);


        List list = addOnArticleRepository.findUrls();
//        Map<Integer, String> repeatMap = new HashMap<>();

        for (Object object : list) {
            try {
                Object[] objects = (Object[]) object;
                Integer id = Integer.parseInt(objects[0].toString());
                String url = objects[1].toString();
                findRepeatLinks(id, url);

//                Archives archives = archivesRepository.findOne(addOnArticle.getAid());
//                String title = archives.getTitle();
//
//                String fromId = null;
//                try {
//                    SourceArticleFour four = sourceArticleFourRepository.findByTitle(title);
//                    if (four == null) {
//                        log.info("error " + title);
//                    } else {
//                        //log.info(four.getFromId());
//                        fromId = four.getFromId();
//                    }
//                } catch (Exception ex) {
//                    log.info("error " + title);
//                }


//                String fromId = addOnArticle.getFromZMZId().toString();
//                if (!StringUtils.isEmpty(fromId)) {
//                    SourceArticleFive four = getZMZData(fromId, webDriver);
//                    if (four != null) {
//                        if (!StringUtils.isEmpty(four.getDownUrls())) {
//                            addOnArticle.setDownUrls(four.getDownUrls());
////                            addOnArticle.setFromZMZId(Integer.parseInt(fromId));
//                            addOnArticleRepository.save(addOnArticle);
//                            log.info(addOnArticle.getAid() + " do");
//                        } else {
//                            log.error(addOnArticle.getAid() + " " + addOnArticle.getMovieName() + " pull null 1");
//                        }
//                    } else {
//                        log.error(addOnArticle.getAid() + " " + addOnArticle.getMovieName() + " pull null 2");
//                    }
//                }
            } catch (Exception e) {
//                log.error(addOnArticle.getAid() + " " + addOnArticle.getMovieName());
            }
        }

//        List<String> listFrom = new ArrayList<>();
//        Iterator<Map.Entry<Integer, String>> iterator = repeatMap.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<Integer, String> entry = iterator.next();
//            Integer id = entry.getKey();
//            String title = entry.getValue();
//
//
//        }

        //do downUrls
//        webDriver.quit();

        log.info(" do finished findRepeatLink");
    }


//    public SourceArticleFive getZMZDataByUser(String fromId, String theSeason) {
//        SourceArticleFive sourceArticleFour = null;
//        WebDriver driver = null;
//        //one test
//        try {
//            driver = utilsService.getZMZWebDriverByUser();
//            sourceArticleFour = pullService.getOneMovie(driver, fromId, theSeason);
//            sourceArticleFour.setPingfen(getScore(fromId));
//            driver.quit();
//        } catch (Exception e) {
//            log.error("fail", e);
//            if (driver != null) driver.quit();
//
//        }
//
//        return sourceArticleFour;
//    }


    public void findRepeatLinks(Integer id, String downUrls) {
//        String downUrls = addOnArticle.getDownUrls();
        if (!StringUtils.isEmpty(downUrls)) {
            List<String> urls = findLinks(downUrls);

            boolean haveRepeat = false;
            List<String> noRepeatUrls = new ArrayList<>();
            String lastUrls = "";
            for (String item : urls) {
                if (lastUrls.equals(item)) {
                    haveRepeat = true;
                    log.info(item);
                    break;
                }
                lastUrls = item;
            }
            if (haveRepeat) {
                log.info(id + " repeat");
            }

//            if (haveRepeat) {
//                Archives archives = archivesRepository.findOne(addOnArticle.getAid());
//                String title = archives.getTitle().replace("迅雷下载", "");
//
//                String fromId = null;
//                try {
//                    SourceArticleThree four = sourceArticleThreeRepository.findByTitle(title);
//                    if (four == null) {
//                        log.info("error " + title);
//                    } else {
//                        //log.info(four.getFromId());
//                        fromId = four.getFromId();
//                    }
//                } catch (Exception ex) {
//                    log.info("error " + title);
//                }
//
//                if (!StringUtils.isEmpty(fromId)) {
//                    SourceArticleFive four = getZMZData(fromId);
//                    addOnArticle.setDownUrls(four.getDownUrls());
//                    addOnArticle.setFromZMZId(Integer.parseInt(fromId));
//                    addOnArticleRepository.save(addOnArticle);
//                    log.info(addOnArticle.getAid());
//                }
//            }
        }
    }

    public void checkImageFileExist() {

        List list = archivesRepository.findImageUrls();
        for (Object object : list) {
            Object[] objects = (Object[]) object;
            Integer id = Integer.parseInt(objects[0].toString());
            String imageUrl = objects[1].toString();
            if (!StringUtils.isEmpty(imageUrl)) {
                String fileUrl = "D:/wwwroot/liuxueba/wwwroot" + imageUrl.replace("-lp.jpg", ".jpg");
                File file = new File(fileUrl);
                if (!file.exists()) {
                    log.info(id + " " + imageUrl);
                }
            }
        }

        log.info(" checkImageFileExist finished");

    }

    /**
     * 获得当前集数(单季)
     *
     * @param downUrls
     * @return
     */
    public int getCurrentCollection(String downUrls) {
        downUrls = utilsService.replaceBlank(downUrls);
        List<String> names;

        if (downUrls.indexOf("<table>") >= 0 || downUrls.indexOf("<table") >= 0) {
            names = utilsService.getHrefNameList(downUrls);  //from tt
        } else {
            names = utilsService.getLiList(downUrls);  //from zmz
        }

        if (names != null && names.size() > 0) {
            List<String> episodes = new ArrayList<>();
            for (String item : names) {
                String episode = utilsService.getEpisode(item);
                if (!StringUtils.isEmpty(episode)) episodes.add(episode);
            }
            int max = 0;
            for (String item : episodes) {
                try {
                    if (item.startsWith("0")) {
                        if (Integer.parseInt(item.substring(1)) > max)
                            max = Integer.parseInt(item.substring(1));
                    } else if (Integer.parseInt(item) > max) {
                        max = Integer.parseInt(item);
                    }
                } catch (Exception e) {
                }
            }
            return max;
        }
        return 0;
    }




}
