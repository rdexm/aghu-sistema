package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;


import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoConvenioOpms;
import br.gov.mec.aghu.dominio.DominioTipoObrigatoriedadeOpms;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcGrupoAlcadaAvalOpms;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastroGrupoAlcadaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	 //this.inicio();
	}

	private static final long serialVersionUID = 3288102063913966552L;
	
	private static final String PESQUISA_GRUPO_ALCADA = "blococirurgico-pesquisaGrupoAlcada";
	
	private static final String MODAL1 = "modalConfirmacao3WG";
	
	private static final String MODAL2 = "modalConfirmacao2WG";

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IAghuFacade aghuFacade;

	private Short grupoAlcadaSeq;

	private Short codigoGrupo;

	private DominioTipoConvenioOpms tipoConvenio;

	private String convenio;

	private AghEspecialidades aghEspecialidades;

	private Short especialidadeSeq;

	private DominioTipoObrigatoriedadeOpms tipoObrigatoriedadeOpms;

	private String obrigatoriedade;

	private DominioSituacao dominioSituacao;

	private MbcGrupoAlcadaAvalOpms grupoAlcadaVersaoAnterior;

	private MbcGrupoAlcadaAvalOpms grupoAlcada = new MbcGrupoAlcadaAvalOpms();

	private Boolean exibeConfirmacaoAlteracaoAtivo;

	private Boolean exibeConfirmacaoNovoGrupo;

	private Boolean limpaCampos = false;
	
	/*
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	private Short grupoAlcadaSeq;
	
	private DominioTipoConvenioOpms tipoConvenio;
	
	private List<HistoricoAlteracoesGrupoAlcadaVO> historicoGrupoAlcada;

	private String convenio;

	private AghEspecialidades aghEspecialidades;

	private Short especialidadeSeq;

	private Short versao; 	

	private String situacao;

	private DominioSituacao dominioSituacao;

	private MbcGrupoAlcadaAvalOpms grupoAlcadaVersaoAnterior;

	private MbcGrupoAlcadaAvalOpms grupoAlcada;

	private Boolean exibeConfirmacao;
	
	private String voltarPara;

	private String tipoConvenioFiltro;
	private Short especialidadeSeqFiltro;
	private Short versaoFiltro;
	private String situacaoFiltro;
	private String tipoResponsabilidadeFiltro;
	
	private Boolean limpaCampos = false;
	
	*/
	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void inicio() {

		if (codigoGrupo != null) {
			this.grupoAlcada = null;
			this.grupoAlcada = blocoCirurgicoCadastroApoioFacade.buscaGrupoAlcadaPorSequencial(codigoGrupo);
			//try {
				//this.grupoAlcadaEdicao = grupoAlcada.clone();
				tipoObrigatoriedadeOpms =  grupoAlcada.getTipoObrigatoriedade();
				
			//} catch (CloneNotSupportedException e) {
			//	apresentarMsgNegocio(Severity.ERROR, "ERRO_CLONAR_GRUPO_ALCADA");
			//}
		}else {
			//this.grupoAlcadaEdicao = new MbcGrupoAlcadaAvalOpms();
			this.grupoAlcada = new MbcGrupoAlcadaAvalOpms();
		}

		if (grupoAlcada.getSituacao() == null) {
			this.grupoAlcada.setAtivo(Boolean.TRUE);
		}

		exibeConfirmacaoAlteracaoAtivo = false;
		super.closeDialog(MODAL1);
		exibeConfirmacaoNovoGrupo = false;
		super.closeDialog(MODAL2);
			

		if (limpaCampos) {
			this.grupoAlcada.setSeq(null);
			this.grupoAlcada.setVersao(null);
			this.grupoAlcada.setSituacao(null);
			this.grupoAlcada.setTipoConvenio(null);
			this.grupoAlcada.setAghEspecialidades(null);
			this.grupoAlcada.setTipoObrigatoriedade(null);
		}
		
	}
	
	public String confirmar() {
		grupoAlcadaVersaoAnterior = this.blocoCirurgicoCadastroApoioFacade.buscarGrupoAlcadaAtivo(grupoAlcada.getTipoConvenio(),grupoAlcada.getAghEspecialidades());
		if (grupoAlcadaVersaoAnterior != null && this.grupoAlcada.getSeq() == null && this.grupoAlcada.getSituacao().equals(DominioSituacao.A)) {
			
			exibeConfirmacaoNovoGrupo = true;
			super.openDialog(MODAL2);
		} else if (grupoAlcadaVersaoAnterior != null && this.grupoAlcada.getSeq() != null && !this.grupoAlcada.getVersao().equals(grupoAlcadaVersaoAnterior.getVersao()) && this.grupoAlcada.getSituacao().equals(DominioSituacao.A)) {

			exibeConfirmacaoAlteracaoAtivo = true;
			super.openDialog(MODAL1);

		} else if (this.grupoAlcada.getSeq() == null) {
			try {
				List<MbcGrupoAlcadaAvalOpms> lista = blocoCirurgicoCadastroApoioFacade.validaGrupoEspecialidadeConvenio(grupoAlcada);
				if(lista != null){
					if(lista.size() > 0){
						this.apresentarMsgNegocio(Severity.ERROR, "LABEL_GRUPO_ALCADA_EXISTE");
						grupoAlcada.setVersao(null);
						return null;
					}
				}
				//MbcGrupoAlcadaAvalOpms novoGrupo = new MbcGrupoAlcadaAvalOpms();
				//novoGrupo.setTipoConvenio(grupoAlcada.getTipoConvenio());
				//novoGrupo.setAghEspecialidades(grupoAlcada.getAghEspecialidades());
				//novoGrupo.setTipoObrigatoriedade(tipoObrigatoriedadeOpms);
				//novoGrupo.setSituacao(grupoAlcada.getSituacao());
				//grupoAlcada.setTipoObrigatoriedade(tipoObrigatoriedadeOpms);
				grupoAlcada.setTipoObrigatoriedade(tipoObrigatoriedadeOpms);
				insereGrupoAlcada();
				return this.cancelar();
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		} else {
			try {
				grupoAlcada.setTipoObrigatoriedade(tipoObrigatoriedadeOpms);
				alteraGrupoAlcada();
				return this.cancelar();
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}

		}
		return null;
	}

	public String confirmaInsercaoGrupoAlcada() {
		try {
			RapServidores rapServidores = registroColaboradorFacade
					.obterServidorAtivoPorUsuario(
							this.obterLoginUsuarioLogado(), new Date());

			dominioSituacao = grupoAlcada.getSituacao();
			this.blocoCirurgicoCadastroApoioFacade.alteraGrupoAnterior(
					grupoAlcadaVersaoAnterior, rapServidores);
			
			grupoAlcada.setSituacao(dominioSituacao);

			insereGrupoAlcada();

			exibeConfirmacaoAlteracaoAtivo = false;
			super.closeDialog(MODAL1);
			exibeConfirmacaoNovoGrupo = false;
			super.closeDialog(MODAL2);
			return cancelar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String confirmaAlteracaoGrupoAlcada() {
		try {
			RapServidores rapServidores = registroColaboradorFacade
					.obterServidorAtivoPorUsuario(
							this.obterLoginUsuarioLogado(), new Date());

			dominioSituacao = grupoAlcada.getSituacao();
			this.blocoCirurgicoCadastroApoioFacade.alteraGrupoAnterior(
					grupoAlcadaVersaoAnterior, rapServidores);

			grupoAlcada.setSituacao(dominioSituacao);

			insereGrupoAlcada();

			exibeConfirmacaoAlteracaoAtivo = false;
			super.closeDialog(MODAL1);
			exibeConfirmacaoNovoGrupo = false;
			super.closeDialog(MODAL2);
			return cancelar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	private void insereGrupoAlcada() throws ApplicationBusinessException {
		salvaGrupoAlcada(grupoAlcada);
		this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_CADASTRO_GRUPO_ALCADA_AVAL_OPME");
	}
	
	private void alteraGrupoAlcada() throws ApplicationBusinessException {
		salvaGrupoAlcada(grupoAlcada);
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_GRUPO_ALCADA_AVAL_OPME");
				
		//this.grupoAlcadaEdicao = null;
	}

	private void salvaGrupoAlcada(MbcGrupoAlcadaAvalOpms grupoAlcada)throws ApplicationBusinessException {
		RapServidores rapServidores = registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado(), new Date());

		grupoAlcada = this.blocoCirurgicoCadastroApoioFacade.persistirGrupoAlcada(grupoAlcada, rapServidores);
		
		this.grupoAlcadaSeq = grupoAlcada.getSeq();
		//this.grupoAlcadaEdicao = null;
	}

	public String cancelar() {
		this.grupoAlcadaVersaoAnterior = null;
		this.codigoGrupo = null;
		this.grupoAlcada = null;
		//this.grupoAlcadaEdicao = null;
		return PESQUISA_GRUPO_ALCADA;
	}

	/**
	 * Obtem especialidade ativa executora de cirurgias
	 */
	public List<AghEspecialidades> obterEspecialidades(String filtro) {
		return this.returnSGWithCount(this.aghuFacade.
				pesquisarEspecialidadesSemEspSeq((String) filtro),obterEspecialidadesCount(filtro));
	}

	public Long obterEspecialidadesCount(String filtro) {
		return this.aghuFacade
				.pesquisarEspecialidadesSemEspSeqCount((String) filtro);
	}

	/**
	 * Obtem unidade funcional ativa executora de cirurgias
	 */
	public List<AghUnidadesFuncionais> obterUnidadeExecutora(Object filtro) {
		return this.aghuFacade.pesquisarUnidadeFuncionalPorSeqDescricao((String) filtro,true);
	}

	public Long obterUnidadeExecutoraCount(Object filtro) {
		return this.aghuFacade.pesquisarUnidadeFuncionalPorSeqDescricaoCount((String) filtro, true);
	}
	
	/*
	 * Getters and Setters abaixo...
	 */
	public IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return blocoCirurgicoCadastroApoioFacade;
	}

	public void setBlocoCirurgicoCadastroApoioFacade(
			IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade) {
		this.blocoCirurgicoCadastroApoioFacade = blocoCirurgicoCadastroApoioFacade;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public void setRegistroColaboradorFacade(
			IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}

	public MbcGrupoAlcadaAvalOpms getGrupoAlcada() {
		return grupoAlcada;
	}

	public void setGrupoAlcada(MbcGrupoAlcadaAvalOpms grupoAlcada) {
		this.grupoAlcada = grupoAlcada;
	}
	
	public MbcGrupoAlcadaAvalOpms getGrupoAlcadaVersaoAnterior() {
		return grupoAlcadaVersaoAnterior;
	}

	public void setGrupoAlcadaVersaoAnterior(
			MbcGrupoAlcadaAvalOpms grupoAlcadaVersaoAnterior) {
		this.grupoAlcadaVersaoAnterior = grupoAlcadaVersaoAnterior;
	}

	public DominioTipoConvenioOpms getTipoConvenio() {
		return tipoConvenio;
	}

	public void setTipoConvenio(DominioTipoConvenioOpms tipoConvenio) {
		this.tipoConvenio = tipoConvenio;
	}

	public AghEspecialidades getAghEspecialidades() {
		return aghEspecialidades;
	}

	public void setAghEspecialidades(AghEspecialidades aghEspecialidades) {
		this.aghEspecialidades = aghEspecialidades;
	}

	public Short getEspecialidadeSeq() {
		return especialidadeSeq;
	}

	public void setEspecialidadeSeq(Short especialidadeSeq) {
		this.especialidadeSeq = especialidadeSeq;
	}

	public DominioSituacao getDominioSituacao() {
		return dominioSituacao;
	}

	public void setDominioSituacao(DominioSituacao dominioSituacao) {
		this.dominioSituacao = dominioSituacao;
	}

	public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}

	public Short getGrupoAlcadaSeq() {
		return grupoAlcadaSeq;
	}

	public void setGrupoAlcadaSeq(Short grupoAlcadaSeq) {
		this.grupoAlcadaSeq = grupoAlcadaSeq;
	}

	public Boolean getLimpaCampos() {
		return limpaCampos;
	}

	public void setLimpaCampos(Boolean limpaCampos) {
		this.limpaCampos = limpaCampos;
	}

	public Short getCodigoGrupo() {
		return codigoGrupo;
	}

	public void setCodigoGrupo(Short codigoGrupo) {
		this.codigoGrupo = codigoGrupo;
	}

	public DominioTipoObrigatoriedadeOpms getTipoObrigatoriedadeOpms() {
		return tipoObrigatoriedadeOpms;
	}

	public void setTipoObrigatoriedadeOpms(
			DominioTipoObrigatoriedadeOpms tipoObrigatoriedadeOpms) {
		this.tipoObrigatoriedadeOpms = tipoObrigatoriedadeOpms;
	}

	public String getObrigatoriedade() {
		return obrigatoriedade;
	}

	public void setObrigatoriedade(String obrigatoriedade) {
		this.obrigatoriedade = obrigatoriedade;
	}

	public Boolean getExibeConfirmacaoAlteracaoAtivo() {
		return exibeConfirmacaoAlteracaoAtivo;
	}

	public void setExibeConfirmacaoAlteracaoAtivo(
			Boolean exibeConfirmacaoAlteracaoAtivo) {
		this.exibeConfirmacaoAlteracaoAtivo = exibeConfirmacaoAlteracaoAtivo;
	}

	public Boolean getExibeConfirmacaoNovoGrupo() {
		return exibeConfirmacaoNovoGrupo;
	}

	public void setExibeConfirmacaoNovoGrupo(Boolean exibeConfirmacaoNovoGrupo) {
		this.exibeConfirmacaoNovoGrupo = exibeConfirmacaoNovoGrupo;
	}
	
}
