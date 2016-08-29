package br.gov.mec.aghu.exames.sismama.action;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSismamaMamoCadCodigo;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.exameselaudos.sismama.vo.AelSismamaMamoResVO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelSerSismama;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class ResultadoMamografiaConclusaoController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 6116321759268506589L;

	@EJB
	private IExamesLaudosFacade examesLaudosFacade;

	private VAelSerSismama responsavel;
	private RapServidores residente;

	// k_variaveis
	private Boolean residenteConectado; // v_residente_conectado

	public void onChangeCategoria(Boolean mamaDireita, Map<String, AelSismamaMamoResVO> mapAbaConclusao) {
		String dominio = "";

		if (mamaDireita) {
			dominio = DominioSismamaMamoCadCodigo.C_CON_RECOM_D.toString();
		} else {
			dominio = DominioSismamaMamoCadCodigo.C_CON_RECOM_E.toString();
		}

		AelSismamaMamoResVO aelSismamaMamoResVO = mapAbaConclusao.get(dominio);
		aelSismamaMamoResVO.setRecomendacao(examesLaudosFacade.onChangeCategoria(mapAbaConclusao, mamaDireita));

		mapAbaConclusao.put(dominio, aelSismamaMamoResVO);
	}

	/**
	 * Pesquisa da suggestion box responsavel
	 * 
	 * @param obj
	 * @return
	 */
	public List<VAelSerSismama> pesquisarResponsavel(String obj) {
		return this.returnSGWithCount(examesLaudosFacade.pesquisarResponsavel(obj),pesquisarResponsavelCount(obj));
	}

	public Long pesquisarResponsavelCount(String obj) {
		return examesLaudosFacade.pesquisarResponsavelCount(obj);
	}

	/**
	 * Pesquisa da suggestion box residente
	 * 
	 * @param obj
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<RapServidores> pesquisarResidente(String obj) throws ApplicationBusinessException {
		return this.returnSGWithCount(examesLaudosFacade.pesquisarResidente(obj),pesquisarResidenteCount(obj));
	}

	public Long pesquisarResidenteCount(String obj) throws ApplicationBusinessException {
		return examesLaudosFacade.pesquisarResidenteCount(obj);
	}

	public VAelSerSismama getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(VAelSerSismama responsavel) {
		this.responsavel = responsavel;
	}

	public RapServidores getResidente() {
		return residente;
	}

	public void setResidente(RapServidores residente) {
		this.residente = residente;
	}

	public Boolean getResidenteConectado() {
		return residenteConectado;
	}

	public void setResidenteConectado(Boolean residenteConectado) {
		this.residenteConectado = residenteConectado;
	}

}
