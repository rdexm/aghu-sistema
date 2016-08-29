package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.math.BigDecimal;
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
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisaNiveisAlcadaAprovacaoPaginatorController extends
		ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	private static final Log LOG = LogFactory.getLog(PesquisaNiveisAlcadaAprovacaoPaginatorController.class);

	private static final long serialVersionUID = -4615760901841389986L;
	
	private static final String MENSAGEM_SUCESSO_EXCLUSAO_SERVIDOR_AVAL_OPME = "MENSAGEM_SUCESSO_EXCLUSAO_SERVIDOR_AVAL_OPME";

	private static final String MENSAGEM_SUCESSO_ALTERACAO_SERVIDOR_AVAL_OPME = "MENSAGEM_SUCESSO_ALTERACAO_SERVIDOR_AVAL_OPME";

	private static final String MENSAGEM_SUCESSO_CADASTRO_SERVIDOR_AVAL_OPME = "MENSAGEM_SUCESSO_CADASTRO_SERVIDOR_AVAL_OPME";
	
	private static final String EDITAR_NIVEL_ALCADA = "blococirurgico-editarNiveisAlcadaAprovacao"; 
	
	private static final String PESQUISA_GRUPO_ALCADA = "pesquisaGrupoAlcada";
	
	private static final String CADASTRO_SERVIDORES = "manterServidorPorNivelAlcadaDeAprovacao";
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
    private List<MbcAlcadaAvalOpms> listaAlcada = null;
	private Collection<MbcAlcadaAvalOpms> selection = new ArrayList<MbcAlcadaAvalOpms>();
	private List<MbcServidorAvalOpms> servidores;
	
	private MbcAlcadaAvalOpms selecionado;
	
	private RapServidores servidorNivelAlcada;

	private String convenio;
	private String situacaoRetorno;
	private String tipoResponsabilidadeFiltro;
	private Short versaoFiltro;
	private String situacaoFiltro;
	private Short seqSelecionado;
	private boolean bloqueiaCampo = false;
	private Short servidorExclusao;
	private Boolean exibirBotaoNovo;
	private Boolean exibeConfirmacao;
	private short grupoAlcadaSeq;

	
	private MbcAlcadaAvalOpms nivelAlcada;
	private MbcServidorAvalOpms servidor;
	private MbcAlcadaAvalOpms nivelAlcadaInsercao = new MbcAlcadaAvalOpms();
	private MbcGrupoAlcadaAvalOpms itemExclusao;
	private MbcGrupoAlcadaAvalOpms grupoAlcada = new MbcGrupoAlcadaAvalOpms();
	private MbcGrupoAlcadaAvalOpms grupoAlcadaVersaoAnterior;
	private DominioTipoResponsabilidadeOPMS tipoResponsabilidade;
	private AghEspecialidades aghEspecialidades;
	
	// Parametros Recebidos de view
	
	private Short codigoGrupo;
	
	private Short nivelAlcadaSeq;
	
	private Short especialidadeSeq;
	
	private DominioSituacao situacao;
	
	private DominioTipoConvenioOpms tipoConvenio;
	
	private String especialidade;
	
	private Short versao;
	
	private String tipoConvenioFiltro;
	
	private Short especialidadeSeqFiltro;
	
	// 
	
	/* iniciar Inclusao - 
	 * Parâmetros Necessários enviados XHTML: limpaCampos = true;
	 */
	
	/* Iniciar Edicao -
	 * Parâmetros Necessários enviados XHTML: grupoAlcada.seq, limpaCampos = false */
	
	/* Níveis Alcada - 
	 * Parâmetros Necessários enviados XHTML: grupoAlcadaSeq, grupoAlcada.seq, grupoAlcada.tipoConvenio, grupoAlcada.situacao,
	 *  grupoAlcada.aghEspecialidades.nomeEspecialidade, grupoAlcada.aghEspecialidades.seq, 
	 *  grupoAlcada.versao, tipoConvenioFiltro, especialidadeSeqFiltro, versaoFiltro, situacaoFiltro */

	public void inicio() {
		/*
		this.codigoGrupo = this.grupoAlcada.getSeq();
		MbcGrupoAlcadaAvalOpms grupo =  blocoCirurgicoCadastroApoioFacade.buscaGrupoAlcadaPorSequencial(Short.valueOf(codigoGrupo));
		
		if(grupo.getAghEspecialidades() != null &&
		   grupo.getTipoConvenio() != null &&
		   grupo.getVersao() != null &&
		   grupo.getSituacao() != null){
			
			setAghEspecialidades(grupo.getAghEspecialidades());
			setTipoConvenio(grupo.getTipoConvenio()); 
			setVersao(grupo.getVersao());
			setSituacao(grupo.getSituacao());

			this.pesquisar();
			this.avaliaValorUltimaAlcada();
			
			if(this.nivelAlcadaSeq!=null){
				buscaServidores(this.nivelAlcadaSeq);
			}
		}*/
		this.nivelAlcadaInsercao = new MbcAlcadaAvalOpms();
		this.servidores = null;
		
		MbcGrupoAlcadaAvalOpms grupo =  blocoCirurgicoCadastroApoioFacade.buscaGrupoAlcadaPorSequencial(grupoAlcadaSeq);
		this.aghEspecialidades = grupo.getAghEspecialidades();
		this.tipoConvenio =grupo.getTipoConvenio(); 
		this.versao=  grupo.getVersao();
		this.situacao =grupo.getSituacao();
		
		this.pesquisar();
		
		this.avaliaValorUltimaAlcada();
		if(this.nivelAlcadaSeq!=null){
			buscaServidores(this.nivelAlcadaSeq);
		}

		nivelAlcada = new MbcAlcadaAvalOpms();
	}
	
	 public void buscaServidores(Short nivelAlcadaSeq){
			try {
				servidores = new ArrayList<MbcServidorAvalOpms>(0);
				nivelAlcada.setSeq(nivelAlcadaSeq);
				nivelAlcada  = blocoCirurgicoCadastroApoioFacade.buscaNivelAlcada(nivelAlcada);
				servidores = blocoCirurgicoCadastroApoioFacade.buscaServidoresPorNivelAlcada(nivelAlcada);
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				LOG.error(e.getMessage(), e);
			}
	 }

	public void buscaServidoresPorNivelAlcada(MbcAlcadaAvalOpms nivelAlcada) {
		seqSelecionado = selecionado.getSeq();
		servidores = new ArrayList<MbcServidorAvalOpms>(0);
		servidores = blocoCirurgicoCadastroApoioFacade.buscaServidoresPorNivelAlcada(selecionado);
		
	}
	
	public void removerServidor() {
		MbcServidorAvalOpms servidor = blocoCirurgicoCadastroApoioFacade
				.buscaServidoresPorSeq(servidorExclusao);

		this.nivelAlcada = servidor.getAlcada();
		blocoCirurgicoCadastroApoioFacade.removerServidor(servidor);

		buscaServidoresPorNivelAlcada(this.nivelAlcada);
		this.apresentarMsgNegocio(Severity.INFO,
				MENSAGEM_SUCESSO_EXCLUSAO_SERVIDOR_AVAL_OPME);
	}

	public void adicionarLinhaServidorAlcada() {
		RapServidoresId id = servidorNivelAlcada.getId();
		servidorNivelAlcada = registroColaboradorFacade.buscarServidor(
				id.getVinCodigo(), id.getMatricula());
		MbcAlcadaAvalOpms alcada;
		try {

			alcada = blocoCirurgicoCadastroApoioFacade
					.buscaNivelAlcada(nivelAlcada);
			servidor = new MbcServidorAvalOpms();
			servidor.setAlcada(alcada);
			servidor.setResponsabilidade(tipoResponsabilidade);
			servidor.setSituacao(situacao);

			RapServidores rapServidorCriacao = registroColaboradorFacade
					.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(),
							new Date());

			servidor.setRapServidorCriacao(rapServidorCriacao);
			servidor.setRapServidores(servidorNivelAlcada);
			servidor.setCriadoEm(new Date());

			blocoCirurgicoCadastroApoioFacade
					.persistirMbcServidorAvalOpms(servidor);

			servidores.add(servidor);
			limparCamposCadastroServidor(servidor);
			this.apresentarMsgNegocio(Severity.INFO,
					MENSAGEM_SUCESSO_CADASTRO_SERVIDOR_AVAL_OPME);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
	}

	private void limparCamposCadastroServidor(MbcServidorAvalOpms servidor2) {
		this.servidorNivelAlcada = new RapServidores();
		this.tipoResponsabilidade = null;
		this.situacao = null;
	}

	public void ativarDesativarServidor(MbcServidorAvalOpms servidor) {
		try {
			DominioSituacao situacao = servidor.getSituacao();

			situacao = situacao.equals(DominioSituacao.A) ? DominioSituacao.I: DominioSituacao.A;

			servidor.setSituacao(situacao);
			servidor.setModificadoEm(new Date());
			RapServidores rapServidoresModificacao = registroColaboradorFacade
					.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(),
							new Date());

			servidor.setRapServidoresModificacao(rapServidoresModificacao);
			blocoCirurgicoCadastroApoioFacade.ativarDesativarServidor(servidor);
			List<MbcAlcadaAvalOpms> alcadas;

			alcadas = blocoCirurgicoCadastroApoioFacade
					.buscaNiveisAlcadaAprovacaoPorGrupo(codigoGrupo);

			servidor.setAlcada(alcadas.get(0));
			this.apresentarMsgNegocio(Severity.INFO,
					MENSAGEM_SUCESSO_ALTERACAO_SERVIDOR_AVAL_OPME);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
	}
	private void avaliaValorUltimaAlcada() {
		if (listaAlcada.size() > 0) {
			nivelAlcadaInsercao.setValorMinimo(buscarValorMaximo(listaAlcada).add(new BigDecimal("0.01")));
			bloqueiaCampo = true;
		}else{
			nivelAlcadaInsercao.setValorMinimo(null);
			bloqueiaCampo = false;
		}
	}

	private BigDecimal buscarValorMaximo(List<MbcAlcadaAvalOpms> listaAlcadaEvaluated) {
		return listaAlcadaEvaluated.get(listaAlcadaEvaluated.size() - 1).getValorMaximo();
	}

	public void pesquisar() {
		try {
			
			this.listaAlcada = new ArrayList<MbcAlcadaAvalOpms>();
			
			this.listaAlcada.addAll(blocoCirurgicoCadastroApoioFacade.buscaNiveisAlcadaAprovacaoPorGrupo(grupoAlcada.getSeq()));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void adicionar() {
		try {
			RapServidores servidorCriacao = registroColaboradorFacade .obterServidorAtivoPorUsuario(
							this.obterLoginUsuarioLogado(), new Date());
			MbcGrupoAlcadaAvalOpms grupo = this.blocoCirurgicoCadastroApoioFacade
					.buscaGrupoAlcadaPorSequencial(grupoAlcadaSeq);
			this.nivelAlcadaInsercao.setRapServidores(servidorCriacao);
			this.nivelAlcadaInsercao.setRapServidoresModificacao(servidorCriacao);
			this.nivelAlcadaInsercao.setCriadoEm(new Date());
			this.nivelAlcadaInsercao.setModificadoEm(new Date());
			this.nivelAlcadaInsercao.setGrupoAlcada(grupo);
			
			if(nivelAlcadaInsercao.getValorMaximo().equals(nivelAlcadaInsercao.getValorMinimo())){
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_VALIDACAO_DATAS_ALCADA");				
				return;
			}				
			
			this.blocoCirurgicoCadastroApoioFacade.persistirNivelAlcada(this.nivelAlcadaInsercao);
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_CADASTRO_ALCADA_AVAL_OPME");

			atualizarNiveis();
			
			nivelAlcadaInsercao = new MbcAlcadaAvalOpms();
			this.inicio();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	public void atualizarNiveis(){
		List<MbcAlcadaAvalOpms> lista = new ArrayList<MbcAlcadaAvalOpms>();
		try {
			lista = blocoCirurgicoCadastroApoioFacade.buscaNiveisAlcadaAprovacaoPorGrupoValor(grupoAlcada.getSeq());
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
		this.tipoConvenio =  DominioTipoConvenioOpms.getInstance(this.tipoConvenioFiltro);
		this.especialidadeSeq=this.especialidadeSeqFiltro;
		this.versao=this.versaoFiltro;
		this.situacao=(this.situacaoFiltro!=null && !this.situacaoFiltro.isEmpty()) ?DominioSituacao.valueOf(this.situacaoFiltro):null;
		this.codigoGrupo=null;
		this.servidores = null;
		this.nivelAlcadaSeq = null;
        return PESQUISA_GRUPO_ALCADA;
	}

	public String editarNivelAlcada(){
		
		return EDITAR_NIVEL_ALCADA; 
	}
	
	
	//getter and setter
	
	public void selecionaLinha(MbcGrupoAlcadaAvalOpms grupoAlcadaAvalOpms) {
		this.itemExclusao = grupoAlcadaAvalOpms;
	}
	
	public String manterServidores(MbcAlcadaAvalOpms nivelAlcada) {
		this.nivelAlcada = nivelAlcada;
		return CADASTRO_SERVIDORES;
	}
	

	public String iniciarInclusao(){
		
		this.situacaoRetorno = this.situacao != null ? this.situacao.name(): null;
		return "blococirurgico-IniciarInclusao";		

	}
	public List<RapServidores> buscaServidores(Object parametro) {
		return this.registroColaboradorFacade
				.pesquisarServidoresPorVinculoMatriculaDescVinculoNome(parametro);
	}

	public Long buscaServidoresCount(Object objPesquisa) {
		return this.registroColaboradorFacade
				.pesquisarServidoresCount(objPesquisa);
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

	public Short getNivelAlcadaSeq() {
		return nivelAlcadaSeq;
	}

	public void setNivelAlcadaSeq(Short nivelAlcadaSeq) {
		this.nivelAlcadaSeq = nivelAlcadaSeq;
	}
	public Short getEspecialidadeSeq() {
		return especialidadeSeq;
	}
	public void setEspecialidadeSeq(Short especialidadeSeq) {
		this.especialidadeSeq = especialidadeSeq;
	}
	public AghEspecialidades getAghEspecialidades() {
		return aghEspecialidades;
	}
	public void setAghEspecialidades(AghEspecialidades aghEspecialidades) {
		this.aghEspecialidades = aghEspecialidades;
	}
	public String getTipoConvenioFiltro() {
		return tipoConvenioFiltro;
	}
	public void setTipoConvenioFiltro(String tipoConvenioFiltro) {
		this.tipoConvenioFiltro = tipoConvenioFiltro;
	}
	public Short getEspecialidadeSeqFiltro() {
		return especialidadeSeqFiltro;
	}
	public void setEspecialidadeSeqFiltro(Short especialidadeSeqFiltro) {
		this.especialidadeSeqFiltro = especialidadeSeqFiltro;
	}
	public Short getVersaoFiltro() {
		return versaoFiltro;
	}
	public void setVersaoFiltro(Short versaoFiltro) {
		this.versaoFiltro = versaoFiltro;
	}
	public String getSituacaoFiltro() {
		return situacaoFiltro;
	}
	public void setSituacaoFiltro(String situacaoFiltro) {
		this.situacaoFiltro = situacaoFiltro;
	}
	public Short getSeqSelecionado() {
		return seqSelecionado;
	}
	public void setSeqSelecionado(Short seqSelecionado) {
		this.seqSelecionado = seqSelecionado;
	}
	
	public String getSituacaoRetorno() {
		return situacaoRetorno;
	}

	public void setSituacaoRetorno(String situacaoRetorno) {
		this.situacaoRetorno = situacaoRetorno;
	}
	
	public String getTipoResponsabilidadeFiltro() {                                    
		return tipoResponsabilidadeFiltro;                                                 }                                                                                                                                                                             public void setTipoResponsabilidadeFiltro(String tipoResponsabilidadeFiltro) {         	this.tipoResponsabilidadeFiltro = tipoResponsabilidadeFiltro;                      }

	public MbcGrupoAlcadaAvalOpms getItemExclusao() {
		return itemExclusao;
	}

	public void setItemExclusao(MbcGrupoAlcadaAvalOpms itemExclusao) {
		this.itemExclusao = itemExclusao;
	}

	public short getGrupoAlcadaSeq() {
		return grupoAlcadaSeq;
	}

	public void setGrupoAlcadaSeq(short grupoAlcadaSeq) {
		this.grupoAlcadaSeq = grupoAlcadaSeq;
	}

	public MbcAlcadaAvalOpms getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(MbcAlcadaAvalOpms selecionado) {
		this.selecionado = selecionado;
	}
	
	
}