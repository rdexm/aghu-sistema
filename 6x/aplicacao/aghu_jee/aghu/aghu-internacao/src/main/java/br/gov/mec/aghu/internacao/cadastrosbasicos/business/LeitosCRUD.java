package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.dao.AinExtratoLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinInternacaoJnDAO;
import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinLeitosJnDAO;
import br.gov.mec.aghu.internacao.dao.AinMovimentoInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinQuartosDAO;
import br.gov.mec.aghu.internacao.vo.AinLeitosVO;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinInternacaoJn;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinLeitosJn;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

	/**
	 * Classe responsável por prover os métodos de negócio genéricos para a entidade
	 * de Unidade Funcional.
	 */
@Stateless
public class LeitosCRUD extends BaseBusiness {
	
	private static final String MSG_NAO_FOI_POSSIVEL_ENCONTRAR_ALA = "Não foi possível encontrar entidade AghAla para valor ";

	private static final Log LOG = LogFactory.getLog(LeitosCRUD.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AinLeitosDAO ainLeitosDAO;

	@Inject
	private AinInternacaoDAO ainInternacaoDAO;

	@Inject
	private AinQuartosDAO ainQuartosDAO;

	@Inject
	private AinExtratoLeitosDAO ainExtratoLeitosDAO;

	@Inject
	private AinInternacaoJnDAO ainInternacaoJnDAO;

	@Inject
	private AinMovimentoInternacaoDAO ainMovimentoInternacaoDAO;
	
	@EJB
	private IAghuFacade iAghuFacade;
	
	@Inject
	private AinLeitosJnDAO ainLeitosJnDAO;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7252862950758769496L;

	private enum ExceptionCode implements BusinessExceptionCode {
		AIN_00184,
	}

	/**
	 * método que obtem um leito através da chave primária.
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param id
	 * @return
	 */
	public AinLeitos obterLeitoPorId(String id) {
		return this.getAinLeitosDAO().obterPorChavePrimaria(id,AinLeitos.Fields.UNIDADE_FUNCIONAL);
	}
	
	public AinLeitos obterLeitoPorId(String id, Enum... fields) {
		return this.getAinLeitosDAO().obterPorChavePrimaria(id,fields);
	}
	
	public AinLeitos obterLeitoPorId(String id, Enum[] innerFields, Enum [] leftFields) {
		return this.getAinLeitosDAO().obterPorChavePrimaria(id,innerFields, leftFields);
	}	
	
	/**
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param paramentro
	 * @return
	 */
	public List<AinLeitos> pesquisarLeitos(Object strParamentro) {
		String parametro = (String) strParamentro;
		AghAla alaParametro = null;
		
		alaParametro = getAghuFacade().obterAghAlaPorChavePrimaria(parametro);
		if (alaParametro == null) {
			logInfo(MSG_NAO_FOI_POSSIVEL_ENCONTRAR_ALA + parametro);			
		}
		
		return this.getAinLeitosDAO().pesquisarLeitos(parametro, parametro, alaParametro);
	}

	/**
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param paramentro
	 * @return
	 */
	public List<AinLeitos> pesquisarLeitosOrdenado(Object paramentro) {
		return this.getAinLeitosDAO().pesquisarLeitosOrdenado(paramentro);
	}

	/**
	 * Retorna leito desocupado.
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param paramentro
	 * @return
	 */
	public AinLeitos obterLeitoDesocupado(String leito) {
		return this.getAinLeitosDAO().obterLeitoDesocupado(leito);
	}

	/**
	 * Retorna lista de leitos desocupados.
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param paramentro
	 * @return
	 */
	public List<AinLeitos> pesquisarLeitosDesocupados(String parametro) {
		AghAla alaParametro = null;
		
		alaParametro = getAghuFacade().obterAghAlaPorChavePrimaria(parametro);
		if (alaParametro == null) {
			logInfo(MSG_NAO_FOI_POSSIVEL_ENCONTRAR_ALA + parametro);			
		}
		return this.getAinLeitosDAO().pesquisarLeitosDesocupados(parametro, parametro, alaParametro);
	}

	public List<AinLeitos> pesquisarLeitosPorSituacoesDoLeito(Object strParamentro, DominioMovimentoLeito[] situacoesLeito) {
		String parametro = (String) strParamentro;
		AghAla alaParametro = null;
		
		alaParametro = getAghuFacade().obterAghAlaPorChavePrimaria(parametro);
		if (alaParametro == null) {
			logInfo(MSG_NAO_FOI_POSSIVEL_ENCONTRAR_ALA + parametro);			
		}
		
		return this.getAinLeitosDAO().pesquisarLeitosPorSituacoesDoLeito(parametro, parametro, alaParametro, situacoesLeito);
	}
	
	
	/**
	 * Retorna lista de leitos desocupados.
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param paramentro
	 * @return
	 */
	public AinLeitos obterLeitoBloqueado(String leito) {
		return this.getAinLeitosDAO().obterLeitoBloqueado(leito);
	}

	/**
	 * Retorna lista de leitos desocupados.
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param paramentro
	 * @return
	 */
	public List<AinLeitos> pesquisarLeitosBloqueados(Object strParamentro) {
		String parametro = (String) strParamentro;
		AghAla alaParametro = null;
		
		alaParametro = getAghuFacade().obterAghAlaPorChavePrimaria(parametro);
		if (alaParametro == null) {
			logInfo(MSG_NAO_FOI_POSSIVEL_ENCONTRAR_ALA + parametro);			
		}
		
		return this.getAinLeitosDAO().pesquisarLeitosBloqueados(parametro, parametro, alaParametro);
	}

	/**
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param leito
	 * @return
	 */
	public List<AinLeitos> pesquisaLeitoPeloLeitoId(String leito) {
		return this.getAinLeitosDAO().pesquisaLeitoPeloLeitoId(leito);
	}

	/**
	 * Método para obter a descrição completa do leito (leito, andar, ala,
	 * descrição da unidade funcional)
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param numeroQuarto
	 * @return Descrição completa do quarto
	 */
	public String obterDescricaoCompletaLeito(String leitoId) {
		if (leitoId == null || "".equals(leitoId)) {
			return "";
		} else {
			AinLeitos leito = this.obterLeitoPorId(leitoId);
			if (leito.getUnidadeFuncional() == null) {
				return null;
			} else {
				StringBuffer descricao = new StringBuffer();
				descricao.append(leito.getLeitoID()).append("  /  ")
				.append(leito.getUnidadeFuncional().getAndar());
				if (leito.getUnidadeFuncional().getIndAla() != null) {
					descricao.append(' ').append(
							leito.getUnidadeFuncional().getIndAla());
				}
				descricao.append(" - ").append(leito.getUnidadeFuncional().getDescricao());

				return descricao.toString();
			}
		}
	}

	/**
	 * Retorna lista de leitos desocupados.
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AinLeitos> pesquisarLeitosDesocupadosPorLeito(Object parametro) {
		String stParametro = (String) parametro;
		
		return this.getAinLeitosDAO().pesquisarLeitosDesocupadosPorLeito(stParametro);
	}

	public boolean permitirInternar(String leitoId) {
		AinLeitos leito = getAinLeitosDAO().obterPorChavePrimaria(leitoId);
		DominioMovimentoLeito ml = leito.getTipoMovimentoLeito().getGrupoMvtoLeito();
		return leito.getIndSituacao().equals(DominioSituacao.A)
				&& (ml.equals(DominioMovimentoLeito.L) || ml.equals(DominioMovimentoLeito.R));
	}

	/*
	 * Verifica disponibilidade do leito para internação. ORADB: Procedure
	 * AINP_TRAZ_LTO da AINF_DISP_LEITOS.pll ORADB: View V_AIN_LTO_DISP_RESERVA
	 */
	public void verificarInternar(String leitoId) throws ApplicationBusinessException {
		if (!permitirInternar(leitoId)) {
			throw new ApplicationBusinessException(ExceptionCode.AIN_00184);
		}
	}

	public List<AinLeitos> pesquisarLeitosAtivosDesocupados(String strPesquisa) {
		return this.getAinLeitosDAO().pesquisarLeitosAtivosDesocupados(strPesquisa);
	}

	protected AinQuartosDAO getAinQuartosDAO() {
		return ainQuartosDAO;
	}

	protected AinMovimentoInternacaoDAO getAinMovimentoInternacaoDAO() {
		return ainMovimentoInternacaoDAO;
	}

	protected AinInternacaoDAO getAinInternacaoDAO() {
		return ainInternacaoDAO;
	}

	protected AinExtratoLeitosDAO getAinExtratoLeitosDAO() {
		return ainExtratoLeitosDAO;
	}

	protected AinInternacaoJnDAO getAinInternacaoJnDAO() {
		return ainInternacaoJnDAO;
	}
	
	protected AinLeitosDAO getAinLeitosDAO() {
		return ainLeitosDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}
	
	public AinLeitosJnDAO getAinLeitosJnDAO() {
		return ainLeitosJnDAO;
	}
	
	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	public void setServidorLogadoFacade(IServidorLogadoFacade servidorLogadoFacade) {
		this.servidorLogadoFacade = servidorLogadoFacade;
	}

	public List<AinLeitos> getLeitosPorQuarto(AinQuartos quarto) {
		return getAinLeitosDAO().getLeitosPorQuarto(quarto);
	}

	public void excluirLeitoSemMovimentacao(AinLeitos leito) {
		persistirLeitoJn(leito);
		getAinLeitosDAO().removerComDependencias(leito);
		getAinLeitosDAO().flush();
	}
	
	public void persistirLeitoJn(AinLeitos leito) {
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		AinLeitosJn ainLeitosJn = new AinLeitosJn();
		ainLeitosJn.setLeitoId(leito.getLeitoID());
		ainLeitosJn.setNomeUsuario(servidorLogado.getUsuario());
		ainLeitosJn.setSerMatricula(servidorLogado.getId().getMatricula());
		ainLeitosJn.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		ainLeitosJn.setOperacao(DominioOperacoesJournal.DEL);
		ainLeitosJn.setLeito(leito.getLeito());
		ainLeitosJn.setEspSeq(leito.getEspecialidade().getSeq());
		ainLeitosJn.setNumeroQuarto(leito.getQuarto().getNumero());
		ainLeitosJn.setIndConsEsp(leito.getIndConsEsp());
		ainLeitosJn.setIndPertenceRefl(leito.getIndConsEsp());
		ainLeitosJn.setIndConsClinUnidade(leito.getIndConsClinUnidade());
		ainLeitosJn.setIndBloqLeitoLimpeza(leito.getIndBloqLeitoLimpeza());
		ainLeitosJn.setTipoMovimentoLeito(leito.getTipoMovimentoLeito().getCodigo());
		ainLeitosJn.setUnidadeFuncional(leito.getUnidadeFuncional().getSeq());
		ainLeitosJn.setIndAcompanhamentoCcih(leito.getIndAcompanhamentoCcih());
		ainLeitosJn.setIndSituacao(leito.getIndSituacao());
		getAinLeitosJnDAO().persistir(ainLeitosJn);
		
	}

	public Boolean leitoPossuiMovimentacao(AinLeitosVO leito) {
		Boolean leitoPossuiMovimentacao;
		List<AinInternacao> listaInt = this.getAinInternacaoDAO().verificaLeitoPossuiMovimentacao(leito);
		boolean hasInternacao = (listaInt != null && !listaInt.isEmpty());
		List<AinInternacaoJn> listaIntJN = this.getAinInternacaoJnDAO().verificaLeitoPossuiInternacaoExtornada(leito);
		boolean hasInternacaoJN = (listaIntJN != null && !listaIntJN.isEmpty());
		List<AinMovimentosInternacao> listaMov = getAinMovimentoInternacaoDAO().getListaMovimentoInternacao(leito);
		boolean hasMovimeto = (listaMov != null && !listaMov.isEmpty());
		Long countListaExtLeito = getAinExtratoLeitosDAO().contarMovimentacaoExtratoLeito(leito);
		boolean hasExtLeito = (countListaExtLeito > 1);
		leitoPossuiMovimentacao = (hasInternacao || hasMovimeto || hasInternacaoJN || hasExtLeito); 
		return leitoPossuiMovimentacao;
	}

}
