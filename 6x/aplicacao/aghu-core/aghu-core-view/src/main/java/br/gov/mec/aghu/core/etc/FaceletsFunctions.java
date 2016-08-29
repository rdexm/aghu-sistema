package br.gov.mec.aghu.core.etc;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

import br.gov.mec.aghu.core.action.SecurityController;


public final class FaceletsFunctions {

	/**
	 * Retorna um valor padrão caso o valor informado seja nulo.
	 * 
	 * @param valueToTest
	 *            valor a ser informado
	 * @param defaultValue
	 *            valor padrão
	 * @return retorna o valor padrão se o valor informado for nulo
	 */
	public static Object getDefaultValue(Object valueToTest, Object defaultValue) {
		return valueToTest == null ? defaultValue : valueToTest;
	}

	/**
	 * getDominioItens
	 * 
	 * @param dominioPath
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Enum[] getEnumItens(String enumPath, String enumName) {
		if (enumName==null || enumName.isEmpty()){
			return null;
		}
		try {
			Class<Enum> dominio;
			StringBuffer enumClass=new StringBuffer();
			if (enumPath!=null && !enumPath.isEmpty() && !enumName.contains(".")){
				enumClass.append(enumPath).append('.');
			}
			enumClass.append(enumName);
			dominio = (Class<Enum>) Class.forName(enumClass.toString());
			return (Enum[]) dominio.getDeclaredMethod("values").invoke(null);			
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * customPrefixAjaxRender
	 * 
	 * @param ids
	 * @return
	 */	
	public static String customPrefixAjaxRender(String ids){
		if (ids==null || ids.isEmpty()){
			return "";
		}
		String separator=" ";
		StringBuffer result=new StringBuffer();
		if (ids.contains(",")){
			separator=",";
		}
		for (String nid : ids.split(separator)){
			result.append(' ');
			if (nid.contains("@")){
				result.append(nid.trim());
			}else{
				result.append("@(#");
				result.append(nid.trim());
				result.append(')');
			}			
		}
		return result.toString().trim();
	}	
	

	
	
	public static String gerarMaxValue(Integer maxlenght, Integer decimais) {
		StringBuffer mascara = new StringBuffer();

		for (int i = 0; i < maxlenght; i++) {
			mascara.append('9');
		}
		
		if (decimais != 0) {
			mascara.append('.');

			for (int i = 0; i < decimais; i++) {
				mascara.append('9');
			}

		}

		return mascara.toString();
	}
	
	
	public static String gerarDatePattern(String tipo) {
		switch (tipo) {
		case "data":
			return "dd/MM/yyyy";
		case "hora":
			return "HH:mm";
		case "datahora":
			return "dd/MM/yyyy HH:mm";		
		default:
			return null;
		}
	}
	
	public static String gerarMascaraInputTextData(String tipo) {
		switch (tipo) {
		case "data":
			return "99/99/9999";
		case "hora":
			return "99:99";
		case "datahora":
			return "99/99/9999 99:99";		
		default:
			return null;
		}
	}

	/**
	 * Concatena duas Strings
	 * 
	 * @param a
	 *            String a ser concatenada
	 * @param b
	 *            String a ser concatenada
	 * @return String a + b concatenadas.
	 */
	public static String concat(String a, String b) {
		return a.concat(b);
	}
	
	/**
	 * hasPermission
	 * 
	 * @param action
	 * @param permission
	 * @return
	 */
	public static boolean hasPermission(Object action, String permission){
		return ((SecurityController)action).usuarioTemPermissao(permission);
	}
	
	
	/**
	 * Obtem o valor de uma propriedade aninhada da página.
	 * 
	 * @param bean
	 * @param property
	 * @return
	 */
	public static Object getProperty(String property, Object bean) {
		if (property==null || bean==null){
			return null;
		}
		try {
			if (property.contains(".")){
					return PropertyUtils.getNestedProperty(bean, property);
			}else{
				return PropertyUtils.getProperty(bean, property);
			}
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			return null;
		} catch (org.apache.commons.beanutils.NestedNullException e) {
			return null;
		}	
		
	}	
}