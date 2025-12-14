const DISTANCE = 50; // 上升距离
const DURATION = 500; // 动画时长
const animationMap = new WeakMap(); // 避免内存泄漏

// 观察器初始化
const observer = new IntersectionObserver(entries => {
	entries.forEach(entry => {
		// 进入视口
		if (entry.isIntersecting) {
			// 获取该元素的动画
			const animation = animationMap.get(entry.target);
			// 播放动画
			animation.play();
			// 停止观察该元素
			observer.unobserve(entry.target);
		}
	});
});

// 判断元素是否在视口下方 避免不必要的动画初始化
const isBelowViewport = (el) => {
	const rect = el.getBoundingClientRect();
	return rect.top > window.innerHeight;
}

export default {

	mounted(el) {
		// 若元素在视口下方
		if (!isBelowViewport(el)) {
			return;
		}

		const animation = el.animate([
			{
				transform: `translateY(${DISTANCE}px)`,
				opacity: 0
			},
			{
				transform: 'translateY(0)',
				opacity: 1
			}
		], {
			duration: DURATION,
			easing: 'ease-in-out'
		});
		// 暂停动画
		animation.pause();
		// 存储动画
		animationMap.set(el, animation);
		// 观察元素
		observer.observe(el);
	},
	unMounted(el) {
		// 停止观察元素
		observer.unobserve(el);
	}
}