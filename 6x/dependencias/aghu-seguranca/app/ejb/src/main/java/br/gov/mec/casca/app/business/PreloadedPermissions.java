package br.gov.mec.casca.app.business;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.gov.mec.casca.security.vo.PermissaoVO;

@Name("preloadedPermissions")
@Scope(ScopeType.SESSION)
public class PreloadedPermissions {

	private String aplicacoes;

	private String targets;

	private String actions;
	
	private List<PermissaoVO> preloadedPermissionList;
	
	private Pattern patternLike = Pattern.compile("(^%?)([a-zA-Z0-9\\/\\:]*)(%?$)");
	
	private Pattern patternAplicacoes;
	
	private Pattern patternTargets;
	
	private Pattern patternActions;
	
	public List<PermissaoVO> getPermissions(String aplicacao, String target, String action) {
		
		// Se nao pertencer ao filtro, retornar null
		if (patternTargets == null || !patternTargets.matcher(target).matches()) {
			return null;
		}
		
		if (patternActions == null || !patternActions.matcher(action).matches()) {
			return null;
		}

		// valor de aplicacoes pode ser null. Caso não seja null, deve fazer parte do filtro
		if (patternAplicacoes != null && !patternAplicacoes.matcher(aplicacao).matches()) {
			return null;
		}
		
		List<PermissaoVO> permissions = new ArrayList<PermissaoVO>();
		
		// FIXME Alterar para um algoritmo mais eficiente
		for (PermissaoVO permission: getPreloadedPermissionList()) {
			// TODO Verificar o que acontece com varias aplicacoes APP:target
			if (permission.getTarget().equals(target) && permission.getAction().equals(action)) {
				permissions.add(permission);
			}
		}
		
		return permissions;
	}
	
	private synchronized List<PermissaoVO> getPreloadedPermissionList() {
		if (preloadedPermissionList == null) {
			preloadedPermissionList = getCascaFacade().obterPermissoesPrecarregaveis(aplicacoes, targets, actions);
		}
		
		return preloadedPermissionList;
	}
	
	protected CascaFacade getCascaFacade() {
		return (CascaFacade)Component.getInstance(CascaFacade.class);
	}
	
	public void setAplicacoes(String aplicacoes) {
		validateParameter(aplicacoes);
		
		patternAplicacoes = createPattern(aplicacoes);
		
		this.aplicacoes = aplicacoes;
	}

	public void setTargets(String targets) {
		validateParameter(targets);
		
		patternTargets = createPattern(targets);

		this.targets = targets;
	}

	public void setActions(String actions) {
		validateParameter(actions);
		
		patternActions = createPattern(actions);

		this.actions = actions;
	}
	
	private void validateParameter(String value) {
		if (!patternLike.matcher(value).matches()) {
			throw new IllegalArgumentException(String.format("Valor invalido '%s'", value));
		}
	}
	
	private Pattern createPattern(String value) {
		Matcher matcher = patternLike.matcher(value);
		
		matcher.find();
		
		String regex = "";

		regex += matcher.group(1).equals("%") ? ".*" : "^";
		regex += matcher.group(2);
		regex += matcher.group(3).equals("%") ? ".*" : "$";
		
		return Pattern.compile(regex);
	}

}
