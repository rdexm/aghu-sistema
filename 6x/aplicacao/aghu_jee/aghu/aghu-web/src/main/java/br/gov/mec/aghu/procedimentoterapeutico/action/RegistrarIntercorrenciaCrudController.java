package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang.StringUtils;

import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.MptTipoIntercorrenciaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class RegistrarIntercorrenciaCrudController extends ActionController{

	private static final String PROCEDIMENTOTERAPEUTICO_REGISTRAR_INTERCORRENCIA = "procedimentoterapeutico-registrarIntercorrencia";

	private static final long serialVersionUID = 3081450180103167881L;

	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	private List<MptTipoIntercorrenciaVO> listaTipoIntercorrencias = new ArrayList<MptTipoIntercorrenciaVO>();
	private Short mptTipoIntercorrencia;
	private String descricaoIntercorrencia;
	private Integer sessao;
	private String telaAnterior;


	@PostConstruct
	public void iniciar() {	
		this.begin(conversation);
		obterListaTipoIntercorrencia();
	}	
	
	public String voltar(){
		if(telaAnterior != null && !telaAnterior.isEmpty()){
			return telaAnterior;
		}
		return PROCEDIMENTOTERAPEUTICO_REGISTRAR_INTERCORRENCIA;
	}
	
	public void gravar(){
		try {
			procedimentoTerapeuticoFacade.gravarIntercorrencia(descricaoIntercorrencia.trim(), mptTipoIntercorrencia, sessao);
			descricaoIntercorrencia = StringUtils.EMPTY;
			mptTipoIntercorrencia = null;
			apresentarMsgNegocio("INTERCORRENCIA_GRAVADA");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void obterListaTipoIntercorrencia(){
		listaTipoIntercorrencias = procedimentoTerapeuticoFacade.obterIntercorrencias();
	}

	public List<MptTipoIntercorrenciaVO> getListaTipoIntercorrencias() {
		return listaTipoIntercorrencias;
	}

	public void setListaTipoIntercorrencias(List<MptTipoIntercorrenciaVO> listaTipoIntercorrencias) {
		this.listaTipoIntercorrencias = listaTipoIntercorrencias;
	}

	public Short getMptTipoIntercorrencia() {
		return mptTipoIntercorrencia;
	}

	public void setMptTipoIntercorrencia(Short mptTipoIntercorrencia) {
		this.mptTipoIntercorrencia = mptTipoIntercorrencia;
	}

	public String getDescricaoIntercorrencia() {
		return descricaoIntercorrencia;
	}

	public void setDescricaoIntercorrencia(String descricaoIntercorrencia) {
		this.descricaoIntercorrencia = descricaoIntercorrencia;
	}

	public Integer getSessao() {
		return sessao;
	}

	public void setSessao(Integer sessao) {
		this.sessao = sessao;
	}

	public String getTelaAnterior() {
		return telaAnterior;
	}

	public void setTelaAnterior(String telaAnterior) {
		this.telaAnterior = telaAnterior;
	}
} 	