import pymysql
from pylab import *
import threading
import matplotlib.pyplot as plt
def connect_to_db():
     connection = pymysql.connect(host='localhost', user= 'root', passwd = "root", db = "test", use_unicode = True, charset = "utf8")
     mycursor = connection.cursor()
     fig = plt.figure()
#     threading.Timer(3, connect_to_db).start()
     sql = "SELECT AVG(responsetime), DATE(hittime) FROM AllData GROUP BY DATE(hittime);"
     mycursor.execute(sql)
     results = mycursor.fetchall()
     dates=[]
     responsetime=[]
     ax1 = fig.add_subplot(221)
     for row in results:
         dates.append(str(row[1]))
         responsetime.append(row[0])
     ax1.set_xlabel('dates')
     ax1.set_ylabel('response time')
     ax1.set_title('Displaying average response time vs dates')
     ax1.plot(dates, responsetime)

     pages = []
     occurence = []
     explode = []

     sql = "SELECT COUNT(*),PAGE,max(responsecode) FROM AllData GROUP BY pAGE, CASE WHEN responsecode LIKE '1%' THEN 1 WHEN responsecode LIKE '2%' THEN 2 WHEN responsecode LIKE '3%' THEN 3 WHEN responsecode LIKE '4%' THEN 4 WHEN responsecode LIKE '5%' THEN 5 ELSE NULL END"
     mycursor.execute(sql)
     results = mycursor.fetchall()
     codes100to200 = []
     codes200to300 = []
     codes300to400 = []
     codes400to500 = []
     codes500 = []
     index = 0
     values = []
     ax2 = fig.add_subplot(223)
     for row in results:
         values.append(row[0])
         if not pages:
             pages.append(row[1])
         if row[1] not in pages:
             pages.append(row[1])
             if len(codes100to200) == index:
                 codes100to200.append(0)
             if len(codes200to300) == index:
                 codes200to300.append(0)
             if len(codes300to400) == index:
                 codes300to400.append(0)
             if len(codes400to500) == index:
                 codes400to500.append(0)
             if len(codes500) == index:
                 codes500.append(0)
             index = index + 1
             if 100 <= int(row[2]) < 200:
                 codes100to200.append(row[0])
             elif 200 <= int(row[2]) < 300:
                 codes200to300.append(row[0])
             elif 300 <= int(row[2]) < 400:
                 codes300to400.append(row[0])
             elif 400 <= int(row[2]) < 500:
                 codes400to500.append(row[0])
             elif 500 <= int(row[2]) < 600:
                 codes500.append(row[0])
         else:
             if 100 <= int(row[2]) < 200:
                 codes100to200.append(row[0])
             elif 200 <= int(row[2]) < 300:
                 codes200to300.append(row[0])
             elif 300 <= int(row[2]) < 400:
                 codes300to400.append(row[0])
             elif 400 <= int(row[2]) < 500:
                 codes400to500.append(row[0])
             elif 500 <= int(row[2]) < 600:
                 codes500.append(row[0])

     ind = np.arange(len(pages))
     width = 0.35
     p1 = ax2.bar(ind, codes100to200, width, color=(0.2588, 0.4433, 1.0))
     p2 = ax2.bar(ind, codes200to300, width, color=(1.0, 0.5, 0.62), bottom=[sum(x) for x in zip(codes100to200)])
     p3 = ax2.bar(ind, codes300to400, width, color=(0.2188, 0.1433, 1.0),
                  bottom=[sum(x) for x in zip(codes100to200, codes200to300)])
     p4 = ax2.bar(ind, codes400to500, width, color=(1.0, 0.5, 0.22),
                  bottom=[sum(x) for x in zip(codes100to200, codes200to300, codes300to400)])
     p5 = ax2.bar(ind, codes500, width, color=(1.0, 0.5, 0.62),
                  bottom=[sum(x) for x in zip(codes100to200, codes200to300, codes300to400, codes400to500)])

     ax2.set_yticklabels(np.arange(0, max(values) + 20, 5))
     ax2.set_title('Displaying responses of pages')
     plt.xticks(ind, pages, rotation=90)
     ax2.set_xlabel('page')
     ax2.set_ylabel('occurence')
     ax2.legend((p1[0], p2[0], p3[0], p4[0], p5[0]), ('100-199', '200-299', '300-399', '400-499', '500 and above'))

     #------------
     ax3 = fig.add_subplot(222)

     mycursor = connection.cursor()
     sql = "SELECT COUNT(*),page FROM AllData WHERE DATE(hittime)=CURRENT_DATE AND hittime > DATE_SUB(CURDATE(), INTERVAL 1 HOUR) GROUP BY page;"
     mycursor.execute(sql)
     results = mycursor.fetchall()
     pages = []
     occurence = []
     explode = []
     for row in results:
         print(row)
         occurence.append(row[0])
         pages.append(row[1])
         explode.append(0)
     ax3.pie(occurence, explode=explode, labels=pages, autopct='%1.1f%%',
             shadow=True, startangle=90)
     ax3.axis('equal')  # Equal aspect ratio ensures that pie is drawn as a circle.
     #---------
     plt.tight_layout()
     plt.show()
     time.sleep(20)
connect_to_db()


