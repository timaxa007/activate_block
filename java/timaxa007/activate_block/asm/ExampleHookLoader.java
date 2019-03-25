package timaxa007.activate_block.asm;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import gloomyfolken.hooklib.minecraft.HookLoader;
import gloomyfolken.hooklib.minecraft.PrimaryClassTransformer;

public class ExampleHookLoader extends HookLoader {

	// включает саму HookLib'у. Делать это можно только в одном из HookLoader'ов.
	// При желании, можно включить gloomyfolken.hooklib.minecraft.HookLibPlugin и не указывать здесь это вовсе.
	@Override
	public String[] getASMTransformerClass() {
		return new String[]{PrimaryClassTransformer.class.getName()};
	}

	@Override
	public void registerHooks() {
		//регистрируем класс, где есть методы с аннотацией @Hook

		//https://forum.mcmodding.ru/threads/%D0%93%D0%B0%D0%B9%D0%B4-%D0%9B%D0%B5%D0%B3%D0%BA%D0%BE-1-6-%D0%9C%D0%BE%D0%B4%D0%B8%D1%84%D0%B8%D0%BA%D0%B0%D1%86%D0%B8%D1%8F-%D1%87%D1%83%D0%B6%D0%BE%D0%B3%D0%BE-%D0%BA%D0%BE%D0%B4%D0%B0-%D0%BF%D1%80%D0%B8-%D0%B7%D0%B0%D0%BF%D1%83%D1%81%D0%BA%D0%B5-%D1%82%D1%80%D0%B0%D0%BD%D1%81%D1%84%D0%BE%D0%BC%D0%B5%D1%80%D1%8B.6352/post-156480
		if (FMLLaunchHandler.side().isClient())
			registerHookContainer("timaxa007.activate_block.asm.AnnotationHooksClient");

		registerHookContainer("timaxa007.activate_block.asm.AnnotationHooksCommon");
	}

}
