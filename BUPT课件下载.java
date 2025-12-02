// =================== 最终版配置 ===================

// 1. 设置页数
var totalPages = 5; 

// 2. 只需要填这一行，把那个很长的 GetPage 链接贴这里
// (保留里面的 &img=... 没关系，脚本会自动帮你切掉它)
var rawUrl = 'https://webvpn.bupt.edu.cn/https/77726476706e69737468656265737421e5f44d93323426526b189de29d51367b2f42/office/PW/GetPage?vpn-12-o2-ucloud.bupt.edu.cn&f=ZTpcb2ZmaWNlMzY1XG9mZmljZXdlYlxjYWNoZVxvZmZpY2VcZmlsZXVjbG91ZC5idXB0LmVkdS5jbi40NDNcMS4g6aG555uu566h55CG5qaC6K66LnBkZg==&img=8BTy2gSXw1LS5j7tABkC1v7M3Jj0QlPZU2W*elQvDXmtH9ttaw1jKPw0nOwoAL2HoGfWVxT1bAU-&isMobile=false&hd=&readLimit=&sn=4&furl=&srv=0&revision=-1&comment=-1';

// 3. 图片的基础 URL（一般是固定的，不用改）
var imgBase = 'https://webvpn.bupt.edu.cn/https/77726476706e69737468656265737421e5f44d93323426526b189de29d51367b2f42/office/img?vpn-1&img=';

// ================================================

async function realDownload() {
    console.log("🚀 清洗参数，开始最终尝试...");

    // 【关键步骤】清洗 URL：去掉 &img= 及其后面的所有内容，只保留前面的基础部分
    // 这样服务器就必须听 &sn= 的指挥了
    var cleanBaseUrl = rawUrl.split("&img=")[0]; 

    for (let i = 1; i <= totalPages; i++) {
        try {
            // 1. 构造干净的请求链接，追加 sn 参数
            // 注意：这里手动补全后面的参数，确保格式正确
            let pageApi = cleanBaseUrl + `&sn=${i}&isMobile=false&hd=&readLimit=&furl=&srv=0&revision=-1`;
            
            let res = await fetch(pageApi);
            let json = await res.json();

            // 2. 直接读取 NextPage 字段
            // 根据你之前的截图，图片ID就藏在这里面，不用再去猜了
            let targetId = json.NextPage;

            if (!targetId) {
                console.warn(`⚠️ 第 ${i} 页没有获取到 NextPage ID，JSON如下:`, json);
                continue;
            }

            // 3. 拼接最终图片地址
            let finalImgUrl = imgBase + targetId + "&tp=";

            // 4. 下载
            await downloadImage(finalImgUrl, `slide_${i}.jpg`);
            console.log(`✅ 第 ${i} 页成功 (ID: ${targetId.substring(targetId.length-10)})`);

            // 稍微慢一点，稳一点
            await new Promise(r => setTimeout(r, 1000));

        } catch (e) {
            console.error(`❌ 第 ${i} 页挂了:`, e);
        }
    }
    console.log("🎉 搞定！");
}

async function downloadImage(url, name) {
    const res = await fetch(url);
    if (!res.ok) throw new Error("Status " + res.status);
    const blob = await res.blob();
    if (blob.type.includes("html")) throw new Error("是网页不是图片");
    const a = document.createElement('a');
    a.href = window.URL.createObjectURL(blob);
    a.download = name;
    document.body.appendChild(a);
    a.click();
    a.remove();
}

realDownload();