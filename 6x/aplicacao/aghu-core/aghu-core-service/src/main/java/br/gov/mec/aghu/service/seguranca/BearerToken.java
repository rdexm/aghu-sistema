package br.gov.mec.aghu.service.seguranca;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "BearerToken", description = "Resposta de um pedido de autenticação" )
public class BearerToken implements Serializable {

	private static final long serialVersionUID = 7563215253011951677L;
	
	@ApiModelProperty(value = "Token de acesso", required=true)
	private String acessToken;

	@ApiModelProperty(value = "Tipo do token", required=true)
	private String tokenType;
	
	@ApiModelProperty(value = "Data de Expiração do Token", required=true)
	private Date expirationTime;
	
	@ApiModelProperty(value = "Token para refresh de login", required=true)
	private String refreshToken;
	
	@ApiModelProperty(value = "Informações sobre a autenticação", required=true)
	private String authInfo;
	
	public BearerToken() {
	}

	public String getAcessToken() {
		return acessToken;
	}

	public void setAcessToken(String acessToken) {
		this.acessToken = acessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public Date getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getAuthInfo() {
		return authInfo;
	}

	public void setAuthInfo(String authInfo) {
		this.authInfo = authInfo;
	}

	
}