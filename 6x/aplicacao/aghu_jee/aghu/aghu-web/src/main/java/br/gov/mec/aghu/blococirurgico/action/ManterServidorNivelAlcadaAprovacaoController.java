package br.gov.mec.aghu.blococirurgico.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoConvenioOpms;
import br.gov.mec.aghu.dominio.DominioTipoResponsabilidadeOPMS;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcGrupoAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcServidorAvalOpms;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterServidorNivelAlcadaAprovacaoController extends
		ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ManterServidorNivelAlcadaAprovacaoController.class);

	private static final long serialVersionUID = 3935776944536328322L;

	private Short codigoGrupo;

	private Short codigoNivelAlcada;

	private DominioTipoConvenioOpms tipoConvenio;

	private String convenio;

	private String especialidade;

	private boolean bloqueiaCampo = false;
	
	private Short servidorExclusao;
	
	private Boolean exibirBotaoNovo;

	private Short versao;

	private List<MbcAlcadaAvalOpms> listaAlcada = null;

	private MbcAlcadaAvalOpms nivelAlcadaInsercao = new MbcAlcadaAvalOpms();

	private Collection<MbcAlcadaAvalOpms> selection = new ArrayList<MbcAlcadaAvalOpms>();

	private static final String MENSAGEM_SUCESSO_EXCLUSAO_SERVIDOR_AVAL_OPME = "MENSAGEM_SUCESSO_EXCLUSAO_SERVIDOR_AVAL_OPME";

	private static final String MENSAGEM_SUCESSO_ALTERACAO_SERVIDOR_AVAL_OPME = "MENSAGEM_SUCESSO_ALTERACAO_SERVIDOR_AVAL_OPME";

	private static final String MENSAGEM_SUCESSO_CADASTRO_SERVIDOR_AVAL_OPME = "MENSAGEM_SUCESSO_CADASTRO_SERVIDOR_AVAL_OPME";
	
	private static final String PESQUISA_NIVEL_ALCADA = "pesquisaNiveisAlcadaAprovacao";

	private MbcGrupoAlcadaAvalOpms grupoAlcada = new MbcGrupoAlcadaAvalOpms();

	private MbcGrupoAlcadaAvalOpms grupoAlcadaVersaoAnterior;

	private Boolean exibeConfirmacao;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;

	private DominioSituacao situacao;

	private DominioTipoResponsabilidadeOPMS tipoResponsabilidade;

	private MbcAlcadaAvalOpms nivelAlcada;

	private MbcServidorAvalOpms servidor;

	private RapServidores servidorNivelAlcada;

	private List<MbcAlcadaAvalOpms> niveisAlcada = new ArrayList<MbcAlcadaAvalOpms>(0);

	private List<MbcServidorAvalOpms> servidores;

	private AghEspecialidades aghEspecialidades;

	public void iniciar() {
		try {
			niveisAlcada = new ArrayList<MbcAlcadaAvalOpms>(0);
			nivelAlcada = new MbcAlcadaAvalOpms();
			nivelAlcada.setSeq(codigoNivelAlcada);
			nivelAlcada = blocoCirurgicoCadastroApoioFacade.buscaNivelAlcada(nivelAlcada);
			niveisAlcada.add(nivelAlcada);
			selecionarNivelAlcada(nivelAlcada);
			
			MbcGrupoAlcadaAvalOpms grupo =  blocoCirurgicoCadastroApoioFacade.buscaGrupoAlcadaPorSequencial(codigoGrupo);
			this.aghEspecialidades = grupo.getAghEspecialidades();
			this.tipoConvenio =grupo.getTipoConvenio(); 
			this.versao=  grupo.getVersao();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	

	public void adicionarLinhaAlcada() {
		niveisAlcada.add(nivelAlcada);
	}

	public void selecionarNivelAlcada(MbcAlcadaAvalOpms nivelAlcada) {
		servidores = new ArrayList<MbcServidorAvalOpms>(0);
		servidores = blocoCirurgicoCadastroApoioFacade.buscaServidoresPorNivelAlcada(nivelAlcada);
		this.nivelAlcada = nivelAlcada;
	}

	public void removerServidor() {
		
		MbcServidorAvalOpms servidor=blocoCirurgicoCadastroApoioFacade.buscaServidoresPorSeq(servidorExclusao);
		
		this.nivelAlcada = servidor.getAlcada();
		blocoCirurgicoCadastroApoioFacade.removerServidor(servidor);
		
		selecionarNivelAlcada(this.nivelAlcada);
		this.apresentarMsgNegocio(Severity.INFO,
				MENSAGEM_SUCESSO_EXCLUSAO_SERVIDOR_AVAL_OPME);
		LOG.info(MENSAGEM_SUCESSO_EXCLUSAO_SERVIDOR_AVAL_OPME);
	}
	
	public void ativarDesativarServidor(MbcServidorAvalOpms servidor) {
		try {
			DominioSituacao situacao = servidor.getSituacao();

			situacao = situacao.equals(DominioSituacao.A) ? DominioSituacao.I: DominioSituacao.A;

			servidor.setSituacao(situacao);
			servidor.setModificadoEm(new Date());
			RapServidores rapServidoresModificacao = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(),new Date());
			
			servidor.setAlcada(nivelAlcada);

			servidor.setRapServidoresModificacao(rapServidoresModificacao);
			
			blocoCirurgicoCadastroApoioFacade.ativarDesativarServidor(servidor);

			this.apresentarMsgNegocio(Severity.INFO,
					MENSAGEM_SUCESSO_ALTERACAO_SERVIDOR_AVAL_OPME);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	public void adicionarLinhaServidorAlcada() {
		try {
			RapServidoresId id = servidorNivelAlcada.getId();
			servidorNivelAlcada = registroColaboradorFacade.buscarServidor(id.getVinCodigo(), id.getMatricula());

			MbcAlcadaAvalOpms alcada = blocoCirurgicoCadastroApoioFacade.buscaNivelAlcada(nivelAlcada);
			servidor = new MbcServidorAvalOpms();
			servidor.setAlcada(alcada);
			servidor.setResponsabilidade(tipoResponsabilidade);
			servidor.setSituacao(situacao==null?DominioSituacao.A:situacao);

			RapServidores rapServidorCriacao = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(),new Date());

			servidor.setRapServidorCriacao(rapServidorCriacao);
			servidor.setRapServidores(servidorNivelAlcada);
			servidor.setCriadoEm(new Date());

			blocoCirurgicoCadastroApoioFacade.persistirMbcServidorAvalOpms(servidor);
			
			selecionarNivelAlcada(alcada);
			
			limparCamposCadastroServidor(servidor);
			
			this.apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_CADASTRO_SERVIDOR_AVAL_OPME);
		
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
	}

	private void limparCamposCadastroServidor(MbcServidorAvalOpms servidor2) {
		this.servidorNivelAlcada = null;
		this.tipoResponsabilidade =  null;
		this.situacao = null;
	}

	public void pesquisar() {
		listaAlcada = new ArrayList<MbcAlcadaAvalOpms>();
		try {
			listaAlcada = blocoCirurgicoCadastroApoioFacade
					.buscaNiveisAlcadaAprovacaoPorGrupo(codigoGrupo);
		} catch (ApplicationBusinessException  e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void adicionar() {
		try {
			RapServidores servidorCriacao = registroColaboradorFacade
					.obterServidorAtivoPorUsuario(
							this.obterLoginUsuarioLogado(), new Date());
			MbcGrupoAlcadaAvalOpms grupo = this.blocoCirurgicoCadastroApoioFacade
					.buscaGrupoAlcadaPorSequencial(codigoGrupo);
			this.nivelAlcadaInsercao.setRapServidores(servidorCriacao);
			this.nivelAlcadaInsercao
					.setRapServidoresModificacao(servidorCriacao);
			this.nivelAlcadaInsercao.setCriadoEm(new Date());
			this.nivelAlcadaInsercao.setModificadoEm(new Date());
			this.nivelAlcadaInsercao.setGrupoAlcada(grupo);
			this.blocoCirurgicoCadastroApoioFacade.persistirNivelAlcada(this.nivelAlcadaInsercao);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_ALCADA_AVAL_OPME");
			nivelAlcadaInsercao = new MbcAlcadaAvalOpms();
			this.iniciar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
	}
	
	public String cancelar() {
		servidorNivelAlcada = null;
		tipoResponsabilidade = null;
		situacao = null;
		return PESQUISA_NIVEL_ALCADA;
	}

	public List<RapServidores> buscaServidores(String parametro) throws BaseException {
		return this.returnSGWithCount(this.registroColaboradorFacade.pesquisarServidorPorSituacaoAtivoComUsuario(parametro), this.registroColaboradorFacade.pesquisarServidorPorSituacaoAtivoCount(parametro));
	}
	 
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public DominioTipoResponsabilidadeOPMS getTipoResponsabilidade() {
		return tipoResponsabilidade;
	}

	public void setTipoResponsabilidade(
			DominioTipoResponsabilidadeOPMS tipoResponsabilidade) {
		this.tipoResponsabilidade = tipoResponsabilidade;
	}

	public MbcAlcadaAvalOpms getNivelAlcada() {
		return nivelAlcada;
	}

	public void setNivelAlcada(MbcAlcadaAvalOpms nivelAlcada) {
		this.nivelAlcada = nivelAlcada;
	}

	public List<MbcAlcadaAvalOpms> getNiveisAlcada() {
		return niveisAlcada;
	}

	public void setNiveisAlcada(List<MbcAlcadaAvalOpms> niveisAlcada) {
		this.niveisAlcada = niveisAlcada;
	}

	public MbcServidorAvalOpms getServidor() {
		return servidor;
	}

	public void setServidor(MbcServidorAvalOpms servidor) {
		this.servidor = servidor;
	}

	public IRegistroColaboradorFacade getRegistroColaborador() {
		return getRegistroColaborador();
	}

	public void setRegistroColaborador(
			IRegistroColaboradorFacade registroColaborador) {
		this.registroColaboradorFacade = registroColaborador;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public void setRegistroColaboradorFacade(
			IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}

	public RapServidores getServidorNivelAlcada() {
		return servidorNivelAlcada;
	}

	public void setServidorNivelAlcada(RapServidores servidorNivelAlcada) {
		this.servidorNivelAlcada = servidorNivelAlcada;
	}

	public List<MbcServidorAvalOpms> getServidores() {
		return servidores;
	}

	public void setServidores(List<MbcServidorAvalOpms> servidores) {
		this.servidores = servidores;
	}

	public IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return blocoCirurgicoCadastroApoioFacade;
	}

	public void setBlocoCirurgicoCadastroApoioFacade(
			IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade) {
		this.blocoCirurgicoCadastroApoioFacade = blocoCirurgicoCadastroApoioFacade;
	}

	public Short getCodigoGrupo() {
		return codigoGrupo;
	}

	public void setCodigoGrupo(Short codigoGrupo) {
		this.codigoGrupo = codigoGrupo;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	public Short getVersao() {
		return versao;
	}

	public void setVersao(Short versao) {
		this.versao = versao;
	}

	public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
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

	public Boolean getExibeConfirmacao() {
		return exibeConfirmacao;
	}

	public void setExibeConfirmacao(Boolean exibeConfirmacao) {
		this.exibeConfirmacao = exibeConfirmacao;
	}

	public boolean isBloqueiaCampo() {
		return bloqueiaCampo;
	}

	public void setBloqueiaCampo(boolean bloqueiaCampo) {
		this.bloqueiaCampo = bloqueiaCampo;
	}

	public Boolean getExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(Boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public List<MbcAlcadaAvalOpms> getListaAlcada() {
		return listaAlcada;
	}

	public void setListaAlcada(List<MbcAlcadaAvalOpms> listaAlcada) {
		this.listaAlcada = listaAlcada;
	}

	public MbcAlcadaAvalOpms getNivelAlcadaInsercao() {
		return nivelAlcadaInsercao;
	}

	public void setNivelAlcadaInsercao(MbcAlcadaAvalOpms nivelAlcadaInsercao) {
		this.nivelAlcadaInsercao = nivelAlcadaInsercao;
	}

	public Collection<MbcAlcadaAvalOpms> getSelection() {
		return selection;
	}

	public void setSelection(Collection<MbcAlcadaAvalOpms> selection) {
		this.selection = selection;
	}

	public DominioTipoConvenioOpms getTipoConvenio() {
		return tipoConvenio;
	}

	public void setTipoConvenio(DominioTipoConvenioOpms tipoConvenio) {
		this.tipoConvenio = tipoConvenio;
	}

	public Short getServidorExclusao() {
		return servidorExclusao;
	}

	public void setServidorExclusao(Short servidorExclusao) {
		this.servidorExclusao = servidorExclusao;
	}

	public Short getCodigoNivelAlcada() {
		return codigoNivelAlcada;
	}

	public void setCodigoNivelAlcada(Short codigoNivelAlcada) {
		this.codigoNivelAlcada = codigoNivelAlcada;
	}

	public AghEspecialidades getAghEspecialidades() {
		return aghEspecialidades;
	}

	public void setAghEspecialidades(AghEspecialidades aghEspecialidades) {
		this.aghEspecialidades = aghEspecialidades;
	}
 
}
