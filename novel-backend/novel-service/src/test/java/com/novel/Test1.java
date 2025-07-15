package com.novel;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.novel.dto.book.BookInfoView;
import com.novel.dto.comment.BookCommentView;
import com.novel.dto.home.HomeBookView;
import com.novel.po.Fetchers;
import com.novel.po.Immutables;
import com.novel.po.book.BookInfoTable;
import com.novel.po.comment.BookComment;
import com.novel.po.comment.BookCommentTable;
import com.novel.po.home.HomeBookDraft;
import com.novel.po.home.HomeBookTable;
import com.novel.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.ImmutableObjects;
import org.babyfish.jimmer.jackson.ImmutableModule;
import org.babyfish.jimmer.sql.JSqlClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
public class Test1 {
	
	@Autowired
	private JSqlClient sqlClient;
	
	@Autowired
	private MailService mailService;
	
	private final BookCommentTable bookCommentTable = BookCommentTable.$;
	
	@Test
	public void test() {
		
		String jsonStr = """
          [{"type":0,"bookId":"1334318182169681920","picUrl":"https://www.bqg128.com/bookimg/127/127856.jpg","bookName":"我给战神王爷寄刀片","authorName":"汐娴阳光","bookDesc":"&nbsp;&nbsp;&nbsp;&nbsp;她是冉府的嫡长女确被迫从小离开冉府，他是纪国的战神，更是纪国女子心中的男神。<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;多年后，她学得一身本事再回冉府为母报仇，成了冉府的一抹黑月光，一场巧遇，这抹黑月光成了战神王爷的心尖宝，看黑月光和战神王爷碰撞的爱情火花是闪瞎你的眼还是甜进你的心？<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;有喜欢阳光的小可......爱们，可以加阳光的qq群哟：807833254【展开】【收起】"},{"type":0,"bookId":"1334318497132552192","picUrl":"https://bookcover.yuewen.com/qdbimg/349573/1024476479/600.webp","bookName":"我有家诸天最强当铺","authorName":"白袍小飞","bookDesc":"&nbsp;&nbsp;&nbsp;&nbsp;诸天万界最强当铺，本店物资丰富、价格公道、童叟无欺，只有您想不到的，没有本店没有的。<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;只要您能拿出有价值的东西来典当，本店保证，什么神仙皇帝，在您面前都得靠边站。<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;本店营业地址不定，联系方式暂无，如需交易，请在心中默念：第一当铺，诸天最强，我要交易，快快现身。<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;第一当铺掌柜楚河的私密日记第一页：经营当铺？小意思，不就是做奸商吗，没有人比我更懂做奸商。<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;第一当铺掌柜楚河的私密日记第二页：我从没想过赚穷人的钱，干翻那些大人物，他们的钱就是我的了。"},{"type":0,"bookId":"1334318818323963904","picUrl":"https://bookcover.yuewen.com/qdbimg/349573/1024535258/180.webp","bookName":"名侦探修炼手册","authorName":"肥瓜","bookDesc":"&nbsp;&nbsp;&nbsp;&nbsp;请谨记，永远不要去挖掘那些未被解开的谜题。<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;因为你永远不会知道，当时的人们为了将其掩盖，付出了多大的代价。<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;虚假平静的表面之下，大多数的真相都是残酷的......<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;好吧，我说的再明白点。<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;如果有一天，你在下班的路上看到一本书掉在地上，拴住你的好奇心，千万不要去碰它。<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;......<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;很遗憾，最初的周言并没有认识到这一点。<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;所以他将那本书捡了起来。<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;那书有个很别扭的名字———《名侦探修炼手册》<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;至于作者......叫肥瓜。"},{"type":0,"bookId":"1334328310788882432","picUrl":"https://bookcover.yuewen.com/qdbimg/349573/1021116068/600.webp","bookName":"北国谍影","authorName":"寻青藤","bookDesc":"　　一九三九春，中共地下党员，军统情报站特工许诚言，奉命前往太原锄奸，眼见日寇的残暴，为抗击日寇决定深入敌后，化身蝰蛇，刺杀敌酋，清除汉奸，潜伏敌营，收集情报，在这片北国大地上，续写谍战传奇！北国谍影官方1群：833528943北国谍影官方2群：879936725"},{"type":1,"bookId":"1334334734843609088","picUrl":"https://static.zongheng.com/upload/cover/60/93/6093d1fbf73b4185e400f0c10ec5e5771713754016674.jpeg","bookName":"家里来了个野原琳","authorName":"冰镇菠萝吹雪","bookDesc":"&nbsp;&nbsp;&nbsp;&nbsp;“你愿意获得强大的力量吗?”<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;“我愿意。”<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;“你愿意和一个小美女同居吗?”<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;“我愿意。”<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;“你愿意时不时来一场其它位面双人游吗?”<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;“我都......愿意，可你能给我吗?问这么多。”<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;“契约达成，力量、美女、系统马上发放。”<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;呃……于是乎，主角苏白开局成为十尾人柱力，开启轮回眼仙人体，同时拥有了十二符咒的力量，还有了一个名叫野原琳的同居女友，瞬间这辈子走上了人生巅峰。<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;只是为啥大部分都处于封印中？这要是遇上了带土那个偏执狂怎么办？【展开】【收起】"},{"type":1,"bookId":"1334335471568912384","picUrl":"https://bookcover.yuewen.com/qdbimg/349573/1025459711/150.webp","bookName":"我辈的江湖","authorName":"钟无涯","bookDesc":"&nbsp;&nbsp;&nbsp;&nbsp;下载客户端，查看完整作品简介。"},{"type":1,"bookId":"1337759637059973121","picUrl":"https://bookcover.yuewen.com/qdbimg/349573/1016435430/180.webp","bookName":"檀香刑","authorName":"莫言","bookDesc":"　　《檀香刑》是莫言潜心五年完成的一部长篇力作。在这部神品妙构的小说中，莫言以1900年德国人在山东修建胶济铁路、袁世凯镇压山东义和团运动、八国联军攻陷北京、慈禧仓皇出逃为历史背景，用摇曳多姿的笔触，大悲大喜的激情，高瞻深睿的思想，活龙活现的讲述了发生在\\"高密东北乡\\"的一场可歌可泣的兵荒马乱的运动，一桩骇人听闻的酷刑，一段惊心动魄的爱情。"},{"type":1,"bookId":"1337759773395824640","picUrl":"https://bookcover.yuewen.com/qdbimg/349573/1023831464/150","bookName":"从今天开始做藩王","authorName":"背着家的蜗牛","bookDesc":"　　一闭眼，一睁眼。赵煦发现自己成了一名皇子。美人妖娆，封地很远，国家很乱。而他只想守着自己的封土逍遥自在。只是若有敌人敢来犯，只让他有来无回，心胆寒……"},{"type":1,"bookId":"1337762752513486849","picUrl":"https://bookcover.yuewen.com/qdbimg/349573/1001589072/600.webp","bookName":"明朝那些事儿7","authorName":"当年明月","bookDesc":"　　《明朝那些事儿7:大结局》是对这样一段历史进行了分析梳理，引人思索：明朝最后一位皇帝，自来有许多传说。关于崇祯究竟是一个昏庸无能的皇帝，还是一个力图奋起的人，一直众说纷纭。不管怎么说，这是一个残酷的时代，也是一个精彩的时代。在这一时期，北方的后金势力崛起，经过努尔哈赤的经营，势力急剧壮大。努尔哈赤死后，皇太极即位。袁崇焕就在这一时期迈上了历史的舞台。本文作者告诉我们，袁崇焕这个民族英雄，在历史上不过是个二流角色。"},{"type":1,"bookId":"1337872061003993088","picUrl":"https://bookcover.yuewen.com/qdbimg/349573/1001580389/150","bookName":"酒楼","authorName":"许春樵","bookDesc":"　　天德酒楼传到了齐家三兄弟一代，却被老大一人独占。老三齐立言不愿接受兄长的施舍，也不堪老大的精神虐待，在造汽车的白日梦幻灭后，从最底层做起，历经穷困潦倒和各种打击，终于创办了自己的酒楼，吞并了老大的酒楼。成功后的齐立言却变了，由诚笃奋发的创业者变成了狡诈贪心的资本野心家……"},{"type":1,"bookId":"1337872316667793408","picUrl":"https://img1.doubanio.com/view/subject/s/public/s6933618.jpg","bookName":"大唐太子李建成","authorName":"龙耳东","bookDesc":"　　小说根据宋朝范祖禹、明末清初王夫之，近代章太炎、陈寅恪、郭沫若以及当今世界知名史学家牛致功、胡戟、胡如雷等人的历史研究成果，拨开历史迷雾，还原历史真相。通过李建成短暂而悲剧的人生，告诉您一个波澜壮阔的时代发生的一些惊心动魄的事件，无不令您感慨万千，气荡回肠，掩卷深思，受益良多……"},{"type":1,"bookId":"1337872488235798528","picUrl":"https://bkimg.cdn.bcebos.com/pic/8c1001e93901213fb…t,f_auto/quality,Q_70/resize,m_lfit,limit_1,w_536","bookName":"我的唐山","authorName":"林那北","bookDesc":"　　本书是第一部史诗性再现唐山过台湾历史的长篇巨作。宏大的视野，观照恢弘历史中的纵横捭阖，细致的笔触，抚摸往昔岁月里的爱恨情仇。小说通过一对貌合神离的兄弟和两个性情迥异的女子之间爱恨情仇、波澜起伏的人生悲欢展现整个唐山过台湾斑驳、丰饶而跌宕的历史画卷……"},{"type":1,"bookId":"1337872597996539904","picUrl":"https://www.huaidan263.com/d/file/015508b1e84a1c4ae3b7a78006f9983f.jpg","bookName":"火蓝刀锋","authorName":"冯骥","bookDesc":"　　一次危险的水下救援，将爱吹牛的草根渔民蒋小鱼阴差阳错地进了以残酷训练而著称的“兽营”，在这里，他遇到了一批绝世天才：能在丛林中徒手搏狼的野孩子张冲，全国大学生游泳冠军鲁炎，打遍兽营无敌手的“兽王”巴郎，在海盗窝里艰辛成长的“战神”向羽，草原上的神枪女猎人乌云格日乐等人。"},{"type":1,"bookId":"1337872898115768320","picUrl":"https://bkimg.cdn.bcebos.com/pic/a1ec08fa513d2697b…t,f_auto/quality,Q_70/resize,m_lfit,limit_1,w_536","bookName":"出手","authorName":"陈建波","bookDesc":"　　民国时期，陈仓城，随着陈仓守将党玉昆的覆灭，缙绅名流孙伯啸突然成为各方势力关注的焦点——相传党玉昆曾交他保管一大批藏宝。孙府周围频频上演各种明争暗斗的好戏，共产党、国民党、土匪、日本人、美国人等多方势力互相交错。迷雾重重之中，各方势力一一出手，鹿死谁手？谁能得手？"},{"type":2,"bookId":"1337873148071120896","picUrl":"/image/default.png","bookName":"污点","authorName":"武和平","bookDesc":"　　小说作者既是行动者，又是书写者，还是思想者。他一手用枪，一手用笔，集战士与诗人于一身。他把经验的亲历与人性的剖析结合在一起。他的长篇小说充满正义的激情、直观社会的勇气和法的尊严。小说讲述了一个文物失窃案如何历尽千辛终被破获的故事。其中有动人的爱情故事和感人至深的亲情……"},{"type":2,"bookId":"1337874646456864768","picUrl":"http://www.ailcai.com/d/file/c7ecfc13ec396c65312c2fa68a1e6d5f.jpg","bookName":"黑白道3：渗透","authorName":"朱维坚","bookDesc":"　　洪水中接连出现一男一女两具尸体，李斌良和战友们经缜密侦查，确认二人都是本市应届高中毕业生。为查明真相，他的足迹随着作家的笔迹奔走于各个校园之间，由此他涉足到一个从未涉及过的领域——教育……"},{"type":2,"bookId":"1337874812798767104","picUrl":"https://bookcover.yuewen.com/qdbimg/349573/1030675695/180.webp","bookName":"噬魂","authorName":"通吃小墨墨","bookDesc":"　　这个世界绝对没有绝对的事情，也没有不可能的事。一把充满灵性的枪，一个有着特殊异能的警察，一个能走在人间和冥界的IT人士。一件件无法用现代科学去解释的事件。是世间的恶灵，还是人的心魔？凶手是谁？说不定，他就在看着你……"},{"type":2,"bookId":"1337874951365988352","picUrl":"/image/default.png","bookName":"远征·流在缅北的血","authorName":"金满","bookDesc":"　　远征军第一次入缅作战大溃败，狙击手岳昆仑为追寻大部队孤身进入野人山。在地狱般的野人山中岳昆仑历经生死，与一群战友结下生死情谊。在印度兰姆伽基地整训后岳昆仑跟随部队踏上反攻缅甸的征程，与为兄报仇的天才狙击手藤原冷野不期而遇。"},{"type":2,"bookId":"1337875255574663168","picUrl":"https://www.bqg128.com/bookimg/27/27239.jpg","bookName":"洪荒修圣","authorName":"轩影九变","bookDesc":"　　在他第一次练功的时候，产生的能量波动和天地灵气达到了共振，产生了一个黑洞，将云谣一下子吸了进去。而到的哪个时候，竟然是盘古还没有开天的洪荒时代。那时机遇不断，危险不断的时代。且看现代人云谣怎样玩转圣人，玩转洪荒……"},{"type":3,"bookId":"1337876581629038592","picUrl":"https://bookcover.yuewen.com/qdbimg/349573/1022742133/180.webp","bookName":"诸天之我是沙悟净","authorName":"目自翕张","bookDesc":"　　骆天明本是个农民工，穿越后的身份也和农民工类似，成了挑担子的沙悟净。好在天无绝人之路，他还有个穿越者的标配——系统。只是这个系统也是平民版的，简陋的让人无法直视。骆天明就依靠这个系统，穿越各个位面，见识各种各样的人和事，不断的炼心炼体，一步步走上巅峰！"},{"type":3,"bookId":"1337879143543476224","picUrl":"https://bookcover.yuewen.com/qdbimg/349573/1001591295/150","bookName":"最后一个道士3","authorName":"夏忆","bookDesc":"　　查文斌，凌正阳二十七代传人，茅山天正道掌门，一个因救人触犯了天罚的道士，一个源自生活的人物原型。将带领我们走进一个未知的全新的道家世界，重新打开属于传说中的真实腹地。青衣人，忘川渡人，棺中人，为何都与他有着一致的面孔？师傅、父母、儿女为何一个个都会离他而去？卓雄和大山的身世，血色的纹身究竟象征着什么？一切之前的所有谜团将会在《最后一个道士3》全部揭晓！"},{"type":3,"bookId":"1337879206630002688","picUrl":"https://bookcover.yuewen.com/qdbimg/349573/1001589672/150","bookName":"最后一个道士2","authorName":"夏忆","bookDesc":"　　查文斌——茅山派祖印持有者，正天道最后一代掌教传人。他救人于阴阳之间，却引火烧身，遭天罚阴遣；仗侠肝义胆与一身道术，救活人于阴阳罅隙，渡死人于无间鬼道！身披鱼鳞的远古氐人，复活的神秘君王，真正的扶桑神树······"},{"type":3,"bookId":"1337882606201475072","picUrl":"https://www.bqg128.com/bookimg/163/163410.jpg","bookName":"尸语者","authorName":"法医秦明","bookDesc":"　　20个挑战心理极限的重口味案发现场，20份公安厅从未公开的法医禁忌档案。残忍、变态、惊悚、刺激、真实、震撼！尸语者，与死者朝夕相处的神秘职业，即将剖开震撼人心的亡灵之声！高速公路上抛下9袋尸块，被割下的膀胱里居然藏有冰碴，2000辆飞驰而过的车里，谁才是真正的凶手？垃圾场里被捆绑的女尸，全身器官都已经蜡化，要如何下手，才能验出她死亡的真相？电话打到一半，话筒里却传来沉闷的挣扎声，潜伏在校园当中的魅影，真的吞噬了那些女孩？资深法医老秦亲自捉刀，首度披露惊悚案发细节，创下悬疑小说从未到达的震撼尺度！荒山残尸、灭门惨案、校园禁地、公路游魂、水上浮骸、天外飞尸……每一案都让你无法入睡！"},{"type":3,"bookId":"1337884719262470144","picUrl":"https://www.shuzhaige.com/d/file/c7fe7771ba116ab4db7de6d8467353ea.jpg","bookName":"古董局中局","authorName":"马伯庸","bookDesc":"　　这是一部关于古董鉴定、收藏、造假、设局的百科全书式小说。字画、青铜、金石、瓷器每一件古董背后，都是深厚的历史积淀和文化传承；而每一件仿冒品背后，都是机关算尽的机巧和匪夷所思的圈套。古董造假、字画仿冒，古已有之。东晋时，康昕仿冒王羲之的书法真迹，连他儿子王献之也辨认不出来；宋朝皇帝宋徽宗喜欢造假，仿制了一大批商代的青铜兵器，摆在宫廷里，乐此不疲。在古董斑驳的纹理中，承载着一个民族的文化，一个时代的风貌，它的价值，不是金钱可以衡量的，但可怜的人类却只会用金钱去衡量它。"},{"type":3,"bookId":"1337949399824781312","picUrl":"https://img.22biqu.com/58/58667/58667s.jpg","bookName":"港综1986","authorName":"龙升云霄","bookDesc":"　　五十年代，咏春叶问大战西洋拳王，咏春拳在港岛遍地开花。六十年，五亿探长雷洛一手遮天，组建金钱帝国。七十年，各大公司群雄并起，角逐王者宝座。八十年，群魔乱舞，你方唱罢我登场，弄潮儿各领风骚。说英雄谁是英雄，重生者吕泽仰望天空，立誓要谱写属于自己的传奇。"},{"type":4,"bookId":"1337957605418520576","picUrl":"https://img.c0m.io/quanben5.com/upload/book_images/87/86621.jpg","bookName":"金丹传","authorName":"淮南居士","bookDesc":"　　瑶池浪、多宝、后羿，他们三个一路为了追杀王子魔，三个上级武者，三个疯狂的上级武者，用他们变态的功力，摧毁了整个悬空城，彻底的打碎了悬空城，悬空城从此以后成为了传说。"},{"type":4,"bookId":"1337958745514233856","picUrl":"https://bookcover.yuewen.com/qdbimg/349573/1024222293/150","bookName":"大虞天行","authorName":"叁拾伍","bookDesc":"　　负责降妖除魔的大虞天行府的督丁赵山河醒来后惊骇发现自己竟是被只怨灵害死的。这职业如此危险他正惶恐，忽然察觉到自己识海竟有个书架，吸纳足够的情绪值，就能开启亮起的书本掌握本领。比如美食杂志能让他学会烧菜获得好评，反馈更多的情绪值。比如历代大家附录里的诗词能够提升灵符质地，甚至具现名将强军帮他杀敌。而他每学习一本，书籍还会自动更新其他的新书出来。这厮顿时振奋，于是天下妖魔就此倒了血霉。PS：我叫赵山河，我在大虞很红，因为我是个挂哔。"},{"type":4,"bookId":"1337960531323047936","picUrl":"https://bookcover.yuewen.com/qdbimg/349573/1024139283/150","bookName":"脑海里飘来一座废品收购站","authorName":"鬼谷仙师","bookDesc":"　　物品：雄鹰展翅图！介绍：唐府遗弃之物，唐寅闲暇所作……完整度：20％修复需消耗财富值：50W。——陈牧羽，一个普普通通收破烂的，本以为一生注定平凡，没想到脑海里莫名其妙的飘来一座万界废品收购站，从此，世界变得不同了起来……"},{"type":4,"bookId":"1337961040297644032","picUrl":"https://i2.novel543.com/thumb/120x160/20230825/190128508439.jpg","bookName":"帝王之武修都市","authorName":"奇玄子","bookDesc":"　　一个纵横商界的奇葩人物，意外邂逅一块奇异石头，从此攻城略地崔营拔寨，钱财滚滚美女缠身，成就都市逍遥帝王！"},{"type":4,"bookId":"1337968514828390400","picUrl":"http://www.nnmfwlww.com/images/default.gif","bookName":"极品记者斗僵尸","authorName":"浪子月生","bookDesc":"　　\\"一个胯下有巨物的记者在调查一桩变态的凶杀案时，意外邂逅一名村姑美少妇。从此记者神魂颠倒，千方百计想把少妇泡到手骗上床。不幸的是，每次都是失败而终。正当记者通过不懈努力，一步步接近成功时，一场山村里的僵尸玩过界，正在悄然上演……于是记者展开了一边泡少妇一边斗僵尸的奇异之旅。随着他在两方都一步步接近成功时，最终的僵尸之祖旱魃逐渐在山村中复苏……\\""},{"type":4,"bookId":"1337971702436515840","picUrl":"https://bookcover.yuewen.com/qdbimg/349573/1024517821/150","bookName":"我真不是角色球员","authorName":"易文三不知","bookDesc":"　　最好的蓝领，最完美的拼图，最佳帮手，最优质的的角色球员……《混子》，《夺冠第六人》，《运气论》，《抱腿大师》……李恪：我真不是！"}]
""";
		
		try {
			
			//JSONObject jsonObject = JSON.parseObject(jsonStr);
			JSONArray objects = JSONUtil.parseArray(jsonStr);
			List<HomeBookView> homeBook = JSONUtil.toList(objects, HomeBookView.class);

// 可以解析并封装为Person对象
//			List<HomeBookView> homeBook = JSON.parseArray(jsonStr, HomeBookView.class);
			log.info("execute: {}", homeBook);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	
	
	}
	
	@Test
	public void test2() {
		mailService.sendTextMailMessage("2889157408@qq.com", "精品小说屋", "您的验证码是：446637。5分钟内有效，请尽快验证。请勿泄露您的验证码。");
	}
}
