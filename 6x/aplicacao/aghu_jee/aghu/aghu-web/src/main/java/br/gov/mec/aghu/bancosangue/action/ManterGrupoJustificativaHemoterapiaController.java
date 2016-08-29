package br.gov.mec.aghu.bancosangue.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsGrupoJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.AbsJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.AbsProcedHemoterapico;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterGrupoJustificativaHemoterapiaController extends ActionController {

	private static final long serialVersionUID = -8022167707748325109L;

	private AbsGrupoJustificativaComponenteSanguineo grupo;
	
	private AbsJustificativaComponenteSanguineo justificativa;
	
	private List<AbsJustificativaComponenteSanguineo> justificativas;
	
	private Boolean situacaoGrupo;
	
	private Boolean situacaoJustificativa;
	
	private DominioSimNao descricaoLivre;
	
	private AbsComponenteSanguineo componenteSanguineo;
	
	private AbsProcedHemoterapico procedimentoHemoterapico;
	
	private static final Integer maxWidth = 50;

	private static final String PESQUISAR_GRUPO_JUSTIFICATIVA_HEMOTERAPIA = "bancodesangue-pesquisarGrupoJustificativaHemoterapia";

	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;

	protected enum EnumManterGrupoJustificativaHemoterapiaRetorno {
		MSG_GRUPO_JUSTIFICATIVA_HEMOTERAPIA_GRAVADO_SUCESSO,
		MSG_JUSTIFICATIVA_HEMOTERAPIA_ADICIONADA_SUCESSO,
		MSG_GRUPO_JUSTIFICATIVA_HEMOTERAPIA_ALTERADO_SUCESSO,
		MSG_JUSTIFICATIVA_HEMOTERAPIA_ALTERADO_SUCESSO;
	}
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

	 

		if (grupo != null && grupo.getSeq() != null) {
			grupo =bancoDeSangueFacade.obterGrupoJustificativaComponenteSanguineo(grupo.getSeq());

			if(grupo == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			setSituacaoGrupo(getGrupo().getSituacao().isAtivo());
			setJustificativas(bancoDeSangueFacade.pesquisarJustificativaComponenteSanguineosPorGrupoJustificativaCompomenteSanguineo(grupo.getSeq()));
			inicializarValoresJustificativaComponenteSanguineoDefault();
			
		} else {
			inicializarValoresGrupoJustificativaComponenteSanguineoDefault();
		}
		return null;
	
	}
	
	public void gravar() {
		try {
			if (getSituacaoGrupo()) {
				getGrupo().setSituacao(DominioSituacao.A);
			} else {
				getGrupo().setSituacao(DominioSituacao.I);
			}
			
			boolean atualizado = getGrupo().getSeq() != null;

			bancoDeSangueFacade.persistirGrupoJustificativaComponenteSanguineo(getGrupo());
			inicializarValoresJustificativaComponenteSanguineoDefault();
			incluirMessagem(getGrupo(), atualizado);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void incluirMessagem(Object obj, Boolean atualizado) {
		String messagem = "";
		if (obj instanceof AbsGrupoJustificativaComponenteSanguineo) {
			if (atualizado) {
				messagem = EnumManterGrupoJustificativaHemoterapiaRetorno.MSG_GRUPO_JUSTIFICATIVA_HEMOTERAPIA_ALTERADO_SUCESSO.toString();
			} else  {
				messagem = EnumManterGrupoJustificativaHemoterapiaRetorno.MSG_GRUPO_JUSTIFICATIVA_HEMOTERAPIA_GRAVADO_SUCESSO.toString();
			}			
		} else if (obj instanceof AbsJustificativaComponenteSanguineo) {
			if (atualizado) {
				messagem = EnumManterGrupoJustificativaHemoterapiaRetorno.MSG_JUSTIFICATIVA_HEMOTERAPIA_ALTERADO_SUCESSO.toString();
			} else  {
				messagem = EnumManterGrupoJustificativaHemoterapiaRetorno.MSG_JUSTIFICATIVA_HEMOTERAPIA_ADICIONADA_SUCESSO.toString();
			}	
		}
		apresentarMsgNegocio(Severity.INFO, messagem);			
	}
	
	private void carregarDadosJustificativa() {
		getJustificativa().setGrupoJustificativaComponenteSanguineo(getGrupo());
		if (getSituacaoJustificativa()) {
			getJustificativa().setSituacao(DominioSituacao.A);
		} else {
			getJustificativa().setSituacao(DominioSituacao.I);
		}
		if (getDescricaoLivre() != null) {
			getJustificativa().setDescricaoLivre(getDescricaoLivre().isSim());
		} else {
			getJustificativa().setDescricaoLivre(null);
		}
		getJustificativa().setComponenteSanguineo(getComponenteSanguineo());
		getJustificativa().setProcedimentoHemoterapico(getProcedimentoHemoterapico());
		
	}
	
	public void adicionarJustificativaComponenteSanguineo() {
		try {
			carregarDadosJustificativa();
			boolean atualizado = getJustificativa().getSeq() != null;
			bancoDeSangueFacade.persistirJustificativaComponenteSanguineo(getJustificativa());
			inicializarValoresJustificativaComponenteSanguineoDefault();
			setGrupo(bancoDeSangueFacade.obterGrupoJustificativaComponenteSanguineo(getGrupo().getSeq()));
			setJustificativas(bancoDeSangueFacade.pesquisarJustificativaComponenteSanguineosPorGrupoJustificativaCompomenteSanguineo(getGrupo().getSeq()));
			incluirMessagem(getJustificativa(), atualizado);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void editarJustificativaComponenteSanguineo(AbsJustificativaComponenteSanguineo justificativa) {
		if (justificativa != null) {
			setJustificativa(bancoDeSangueFacade.obterAbsJustificativaComponenteSanguineo(justificativa));
			setSituacaoJustificativa(getJustificativa().getSituacao().isAtivo());
			setProcedimentoHemoterapico(getJustificativa().getProcedimentoHemoterapico());
			setComponenteSanguineo(getJustificativa().getComponenteSanguineo());
			if (getJustificativa().getDescricaoLivre() != null) {
				if (getJustificativa().getDescricaoLivre()) {
					setDescricaoLivre(DominioSimNao.S);				
				} else {
					setDescricaoLivre(DominioSimNao.N);
				}				
			}
		}
	}
	
	public void limparComponenteSanguineo() {
		setComponenteSanguineo(null);
	}
	
	public void limparProcedimentoHemoterapico() {
		setProcedimentoHemoterapico(null);
	}
	
	public String cancelar() {
		limpar();
		return PESQUISAR_GRUPO_JUSTIFICATIVA_HEMOTERAPIA;
	}
	
	public void cancelarEdicaoJustificativa() {
		inicializarValoresJustificativaComponenteSanguineoDefault();
	}
	
	public void limpar() {
		inicializarValoresGrupoJustificativaComponenteSanguineoDefault();
	}
	
	private void inicializarValoresGrupoJustificativaComponenteSanguineoDefault() {
		setGrupo(new AbsGrupoJustificativaComponenteSanguineo());
		setSituacaoGrupo(Boolean.TRUE);
		setJustificativas(new ArrayList<AbsJustificativaComponenteSanguineo>());
		inicializarValoresJustificativaComponenteSanguineoDefault();
	}
	
	private void inicializarValoresJustificativaComponenteSanguineoDefault() {
		setJustificativa(new AbsJustificativaComponenteSanguineo());
		setSituacaoJustificativa(Boolean.TRUE);
		setDescricaoLivre(null);
		limparComponenteSanguineo();
		limparProcedimentoHemoterapico();
	}

	public AbsGrupoJustificativaComponenteSanguineo getGrupo() {
		return grupo;
	}

	public void setGrupo(AbsGrupoJustificativaComponenteSanguineo grupo) {
		this.grupo = grupo;
	}

	public AbsJustificativaComponenteSanguineo getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(AbsJustificativaComponenteSanguineo justificativa) {
		this.justificativa = justificativa;
	}
	
	public Boolean renderJustificativaComponenteSanguineo() {
		return (getGrupo() != null && getGrupo().getSeq() != null);
	}
	
	public String abreviar(String str){
		String abreviado = str;
		if(isAbreviar(str)) {
			abreviado = StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}
	
	public Boolean isAbreviar(String str){
		Boolean abreviar = Boolean.FALSE;
		if (str != null) {
			abreviar = str.length() > maxWidth;
		}
		return abreviar;
	}
	
	public Boolean getSituacaoGrupo() {
		return situacaoGrupo;
	}

	public void setSituacaoGrupo(Boolean situacaoGrupo) {
		this.situacaoGrupo = situacaoGrupo;
	}

	public Boolean getSituacaoJustificativa() {
		return situacaoJustificativa;
	}

	public void setSituacaoJustificativa(Boolean situacaoJustificativa) {
		this.situacaoJustificativa = situacaoJustificativa;
	}

	//Suggestion para  AbsComponenteSanguineos,
	public Long listarAbsComponenteSanguineosCount(String objPesquisa) {
		return bancoDeSangueFacade.listarAbsComponenteSanguineosCount(objPesquisa);
	}
	
	//Suggestion para  AbsComponenteSanguineos,
	public List<AbsComponenteSanguineo> listarAbsComponenteSanguineos(String objPesquisa){
		return this.returnSGWithCount(bancoDeSangueFacade.listarAbsComponenteSanguineos(objPesquisa),listarAbsComponenteSanguineosCount(objPesquisa));
	}
	
	//Suggestion para  AbsProcedHemoterapicos 
	public Long listarAbsProcedHemoterapicosCount(String objPesquisa) {
		return bancoDeSangueFacade.listarAbsProcedHemoterapicoCount(objPesquisa);
	}
	
	//Suggestion para  AbsProcedHemoterapicos 
	public List<AbsProcedHemoterapico> listarAbsProcedHemoterapicos(String objPesquisa){
		return this.returnSGWithCount(bancoDeSangueFacade.listarAbsProcedHemoterapicos(objPesquisa),listarAbsProcedHemoterapicosCount(objPesquisa));
	}

	public List<AbsJustificativaComponenteSanguineo> getJustificativas() {
		return justificativas;
	}

	public void setJustificativas(List<AbsJustificativaComponenteSanguineo> justificativas) {
		this.justificativas = justificativas;
	}

	public DominioSimNao getDescricaoLivre() {
		return descricaoLivre;
	}

	public void setDescricaoLivre(DominioSimNao descricaoLivre) {
		this.descricaoLivre = descricaoLivre;
	}

	public AbsComponenteSanguineo getComponenteSanguineo() {
		return componenteSanguineo;
	}

	public void setComponenteSanguineo(AbsComponenteSanguineo componenteSanguineo) {
		this.componenteSanguineo = componenteSanguineo;
	}

	public AbsProcedHemoterapico getProcedimentoHemoterapico() {
		return procedimentoHemoterapico;
	}

	public void setProcedimentoHemoterapico(AbsProcedHemoterapico procedimentoHemoterapico) {
		this.procedimentoHemoterapico = procedimentoHemoterapico;
	}
}