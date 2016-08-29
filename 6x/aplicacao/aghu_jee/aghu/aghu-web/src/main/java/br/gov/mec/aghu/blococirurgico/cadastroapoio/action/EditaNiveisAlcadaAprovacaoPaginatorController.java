package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.dominio.DominioTipoConvenioOpms;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcGrupoAlcadaAvalOpms;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class EditaNiveisAlcadaAprovacaoPaginatorController extends
		ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	private static final long serialVersionUID = -4615760901841389986L;

	private static final String PESQUISA_NIVEIS_ALCADA ="blococirurgico-pesquisaNiveisAlcadaAprovacao";
	
	private Short codigoNivelAlcada;

	private String convenio;

	private String especialidade;

	private Boolean exibirBotaoNovo;

	private List<MbcAlcadaAvalOpms> alcadas = null;

	private Short nivelExclusao;
	
	private DominioTipoConvenioOpms tipoConvenio;
	
	private AghEspecialidades aghEspecialidades;
	
	private short versao;
	
	private short codigoGrupo;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;

	

	public void inicio() {

		MbcGrupoAlcadaAvalOpms grupo =  blocoCirurgicoCadastroApoioFacade.buscaGrupoAlcadaPorSequencial(codigoGrupo);
		this.aghEspecialidades = grupo.getAghEspecialidades();
		this.tipoConvenio =grupo.getTipoConvenio(); 
		this.versao=  grupo.getVersao();
		this.pesquisar();
	}

	public void pesquisar() {
		alcadas = new ArrayList<MbcAlcadaAvalOpms>();
		try {

			alcadas = blocoCirurgicoCadastroApoioFacade
					.buscaNiveisAlcadaAprovacaoPorGrupo(codigoGrupo);
		
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public Boolean getExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(Boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public List<MbcAlcadaAvalOpms> getAlcadas() {
		return alcadas;
	}

	public IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return blocoCirurgicoCadastroApoioFacade;
	}

	public void setCodigoGrupo(Short codigoGrupo) {
		this.codigoGrupo = codigoGrupo;
	}

	public Short getCodigoGrupo() {
		return codigoGrupo;
	}

	public Short getNivelExclusao() {
		return nivelExclusao;
	}

	public void setNivelExclusao(Short nivelExclusao) {
		this.nivelExclusao = nivelExclusao;
	}

	public void setBlocoCirurgicoCadastroApoioFacadregistroColaboradorFacadee(
			IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade) {
		this.blocoCirurgicoCadastroApoioFacade = blocoCirurgicoCadastroApoioFacade;
	}

	public void salvar() {
		try {
/*
			List<MbcAlcadaAvalOpms> listaCadastrada = blocoCirurgicoCadastroApoioFacade
					.buscaNiveisAlcadaAprovacaoPorGrupo(codigoGrupo);

			if (listaAlcada.equals(listaCadastrada)) {
				getStatusMessages().addFromResourceBundle(Severity.INFO,
						"MENSAGEM_SUCESSO_ALTERACAO_ALCADA_AVAL_OPME");
			} else {*/
			
				this.blocoCirurgicoCadastroApoioFacade.persistirNiveisAlcada(this.alcadas);
				atualizarNiveis();
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_ALCADA_AVAL_OPME");
				this.pesquisar();
			//}
		} catch (BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}

	public void excluir() {
		try {
			
			this.blocoCirurgicoCadastroApoioFacade.removerNivelAlcada(this.nivelExclusao);
			atualizarNiveis();
			alcadas = blocoCirurgicoCadastroApoioFacade.buscaNiveisAlcadaAprovacaoPorGrupo(codigoGrupo);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_ALCADA_AVAL_OPME");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		this.pesquisar();
	}
	
	public void atualizarNiveis(){
		List<MbcAlcadaAvalOpms> lista = new ArrayList<MbcAlcadaAvalOpms>();
		try {
			lista = blocoCirurgicoCadastroApoioFacade.buscaNiveisAlcadaAprovacaoPorGrupoValor(codigoGrupo);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		 Integer nivel = 1;
		 for (MbcAlcadaAvalOpms item : lista) {
			 item.setNivelAlcada(nivel);
			 blocoCirurgicoCadastroApoioFacade.atualizaNivelAlacada(item);
			 nivel++;
		 }
	}

	public String cancelar() {
		return PESQUISA_NIVEIS_ALCADA;
	}

	public Short getCodigoNivelAlcada() {
		return codigoNivelAlcada;
	}

	public void setCodigoNivelAlcada(Short codigoNivelAlcada) {
		this.codigoNivelAlcada = codigoNivelAlcada;
	}

	public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	public DominioTipoConvenioOpms getTipoConvenio() {
		return tipoConvenio;
	}

	public void setTipoConvenio(DominioTipoConvenioOpms tipoConvenio) {
		this.tipoConvenio = tipoConvenio;
	}

	public Short getVersao() {
		return versao;
	}

	public void setVersao(Short versao) {
		this.versao = versao;
	}

	public AghEspecialidades getAghEspecialidades() {
		return aghEspecialidades;
	}

	public void setAghEspecialidades(AghEspecialidades aghEspecialidades) {
		this.aghEspecialidades = aghEspecialidades;
	}
	
}