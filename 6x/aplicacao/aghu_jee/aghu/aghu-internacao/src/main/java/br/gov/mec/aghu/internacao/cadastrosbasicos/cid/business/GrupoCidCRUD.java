package br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.configuracao.dao.AghGrupoCidsDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghCapitulosCid;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghGrupoCids;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de Grupos CIDs por Capítulos.
 */
@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class GrupoCidCRUD extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(GrupoCidCRUD.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IParametroFacade iParametroFacade;
	
	@EJB
	private IAghuFacade iAghuFacade;
	
	@Inject
	private AghGrupoCidsDAO aghGrupoCidsDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4320086670822888027L;
	/**
	 * Constante que guarda o nome do atributo do contexto referente ao sequence
	 * DAO
	 */
	

	private enum AghGrupoCidsCRUDExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_GRUPO_CID, CAPITULO_CID_NAO_ENCONTRADO, CAPITULO_CID_DEVE_ESTAR_ATIVO, AIP_USUARIO_NAO_CADASTRADO, VALORES_MODIFICADOS_INVALIDOS, ERRO_RECUPERACAO_PARAM_P_DIAS_PERM_DEL, REGISTRO_FORA_PERIODO_PERMITIDO_PARA_EXCLUSAO, EXCLUSAO_GRUPO_DEPENTENDES_CID, AGH_GRUPO_SIGLA_JA_CADASTRADO;
	}

	public List<AghGrupoCids> pesquisarGruposCids(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			AghCapitulosCid capitulo, Integer codigoGrupo, String siglaGrupo, String descricaoGrupo, DominioSituacao situacaoGrupo) {
		return this.getAghuFacade().pesquisarGruposCids(firstResult, maxResults, orderProperty, asc, capitulo, codigoGrupo,
				siglaGrupo, descricaoGrupo, situacaoGrupo);
	}

	public Long pesquisarGruposCidsCount(AghCapitulosCid capitulo, Integer codigoGrupo, String siglaGrupo, String descricaoGrupo,
			DominioSituacao situacaoGrupo) {
		return this.getAghuFacade().pesquisarGruposCidsCount(capitulo, codigoGrupo, siglaGrupo, descricaoGrupo, situacaoGrupo);
	}

	public AghGrupoCids obterGrupoCidPorId(Integer cpcSeq, Integer seq) {
		return this.getAghuFacade().obterGrupoCidPorId(cpcSeq, seq);
	}

	/**
	 * Método responsável pela persistência de um grupo para Capítulo CID.
	 * 
	 * @param grupo
	 *            ahgGrupoCid
	 * @throws ApplicationBusinessException
	 */
	public void persistirGrupoCid(AghGrupoCids aghGrupoCid) throws ApplicationBusinessException {
		aghGrupoCid.setSigla(aghGrupoCid.getSigla().toUpperCase());
		aghGrupoCid.setDescricao(aghGrupoCid.getDescricao().toUpperCase());

		if (aghGrupoCid.getSigla() != null) {
			aghGrupoCid.setSigla(aghGrupoCid.getSigla().trim());
		}

		this.verificarSiglaJaExistente(aghGrupoCid);
		if (aghGrupoCid.getId().getSeq() == null) {
			// inclusão
			this.incluirGrupoCid(aghGrupoCid);
		} else {
			// edição
			this.atualizarGrupoCid(aghGrupoCid);
		}
	}

	/**
	 * Método responsável por incluir um novo grupo CID.
	 * 
	 * @param grupo
	 *            de capítuo CID
	 * @throws ApplicationBusinessException
	 */
	private void incluirGrupoCid(AghGrupoCids aghGrupoCid) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.validarDadosGrupoCid(aghGrupoCid);
		aghGrupoCid.setRapServidor(servidorLogado);
		//this.obterValorSequencialId(aghGrupoCid);
		this.getAghGrupoCidsDAO().setarSequencialID(aghGrupoCid);
		aghGrupoCid.setCriadoEm(new Date());
		this.getAghuFacade().inserirAghGrupoCids(aghGrupoCid, true);
	}

	/**
	 * Método que verifica se a sigla atribuída ao grupo CID já não existe para
	 * outro grupo do mesmo capítulo
	 * 
	 * @param aghGrupoCid
	 * @throws ApplicationBusinessException
	 */
	public void verificarSiglaJaExistente(AghGrupoCids aghGrupoCid) throws ApplicationBusinessException {
		AghGrupoCids grupoBanco = this.getAghuFacade().obterGrupoCidPorSigla(aghGrupoCid.getSigla());
		if (grupoBanco != null && (!grupoBanco.getId().getSeq().equals(aghGrupoCid.getId().getSeq()))) {
			throw new ApplicationBusinessException(AghGrupoCidsCRUDExceptionCode.AGH_GRUPO_SIGLA_JA_CADASTRADO);
		}
	}

	/**
	 * Método responsável pelas validações dos dados de grupo para capítulo CID.
	 * Método utilizado para inclusão e atualização de grupo para capítulo CID.
	 * 
	 * @param grupo
	 * @throws ApplicationBusinessException
	 */
	private void validarDadosGrupoCid(AghGrupoCids aghGrupoCid) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		validarCapituloCid(aghGrupoCid);
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(AghGrupoCidsCRUDExceptionCode.AIP_USUARIO_NAO_CADASTRADO);
		}
	}

	/**
	 * Método responsável pelas validações do capítulo CID cuja inclusão de
	 * grupo foi solicitada.
	 * 
	 * @param aghCapituloCid
	 * @throws ApplicationBusinessException
	 */
	private void validarCapituloCid(AghGrupoCids grupoCid) throws ApplicationBusinessException {
		DominioSituacao indSituacao = this.obterSituacaoCapituloCid(grupoCid.getId().getCpcSeq());
		if (indSituacao == null) {
			throw new ApplicationBusinessException(AghGrupoCidsCRUDExceptionCode.CAPITULO_CID_NAO_ENCONTRADO);
		} else if (indSituacao.equals(DominioSituacao.I)) {
			throw new ApplicationBusinessException(AghGrupoCidsCRUDExceptionCode.CAPITULO_CID_DEVE_ESTAR_ATIVO);
		}
	}

	/**
	 * Método que obtem a situação do capítulo CID
	 * 
	 * @param seqCapitulo
	 * @return
	 */
	private DominioSituacao obterSituacaoCapituloCid(Integer seqCapitulo) {
		return this.getAghuFacade().obterSituacaoCapituloCid(seqCapitulo);
	}

	/**
	 * Método responsável pela atualização de um grupo CID.
	 * 
	 * @param grupo
	 *            CID
	 * @throws ApplicationBusinessException
	 */
	private void atualizarGrupoCid(AghGrupoCids aghGrupoCid) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.validarDadosGrupoCid(aghGrupoCid);
		aghGrupoCid.setRapServidor(servidorLogado);
		this.validarAtualizacao(aghGrupoCid);

		this.getAghuFacade().atualizarAghGrupoCids(aghGrupoCid);
	}

	/**
	 * Método responsável pela remoção de um grupo CID.
	 * 
	 * @param grupo
	 *            CID
	 * @throws ApplicationBusinessException
	 */
	public void removerGrupoCid(AghGrupoCids aghGrupoCid) throws ApplicationBusinessException {
		IAghuFacade aghuFacade = this.getAghuFacade();

		this.validarDependencias(aghGrupoCid);
		this.validarRemocao(aghGrupoCid);
		if (!aghuFacade.containsAghGrupoCids(aghGrupoCid)) {
			aghGrupoCid = aghuFacade.mergeAghGrupoCids(aghGrupoCid);
		}
		aghuFacade.removerAghGrupoCids(aghGrupoCid);
	}

	/**
	 * Método responsável pelas validações de atualização de um grupo CID
	 * 
	 * @param aghGrupoCid
	 * @throws ApplicationBusinessException
	 */
	private void validarAtualizacao(AghGrupoCids aghGrupoCid) throws ApplicationBusinessException {

		if (valoresModificados(aghGrupoCid)) {
			throw new ApplicationBusinessException(AghGrupoCidsCRUDExceptionCode.VALORES_MODIFICADOS_INVALIDOS);
		}

	}

	/**
	 * Método responsável pelas validações de remoção de um grupo CID.
	 * 
	 * @param aghGrupoCid
	 * @throws ApplicationBusinessException
	 */
	private void validarRemocao(AghGrupoCids aghGrupoCid) throws ApplicationBusinessException {

		final String nome = "P_DIAS_PERM_DEL_AGH";
		BigDecimal vlrNumerico = this.obterValorNumericoAghParametros(nome);
		if (vlrNumerico == null) {
			throw new ApplicationBusinessException(AghGrupoCidsCRUDExceptionCode.ERRO_RECUPERACAO_PARAM_P_DIAS_PERM_DEL);
		}

		Long diferencaEmDias = diferencaEmDias(aghGrupoCid.getCriadoEm(), new Date());
		if (diferencaEmDias.intValue() > vlrNumerico.intValue()) {
			throw new ApplicationBusinessException(AghGrupoCidsCRUDExceptionCode.REGISTRO_FORA_PERIODO_PERMITIDO_PARA_EXCLUSAO);
		}
	}

	/**
	 * Método responsável por verificar se o grupo CID a ser removido possui
	 * dependências na tabela CID, o que impede a sua remoção.
	 * 
	 * @param aghGrupoCid
	 * @throws ApplicationBusinessException
	 */
	private void validarDependencias(AghGrupoCids aghGrupoCid) throws ApplicationBusinessException {
		Integer cpcSeq = aghGrupoCid.getId().getCpcSeq();
		Integer seq = aghGrupoCid.getId().getSeq();

		List<AghCid> listaCids = this.getAghuFacade().listarCids(cpcSeq, seq);
		if (listaCids.size() != 0) {
			throw new ApplicationBusinessException(AghGrupoCidsCRUDExceptionCode.EXCLUSAO_GRUPO_DEPENTENDES_CID);
		}
	}

	/**
	 * Método que calcula a diferença em dias entre duas datas.
	 * 
	 * @param data1
	 *            , data2
	 * @return
	 */
	private Long diferencaEmDias(Date data1, Date data2) {
		long diferenca = data2.getTime() - data1.getTime();
		long diferencaEmDias = diferenca / (1000 * 60 * 60 * 24);

		return diferencaEmDias;
	}

	/**
	 * Método que obtem o valor do campo vlrNumerico da tabela AGH_PARAMETROS de
	 * acordo com o nome informado
	 * 
	 * @param nome
	 * @return
	 */
	private BigDecimal obterValorNumericoAghParametros(String nome) {
		return this.getParametroFacade().obterValorNumericoAghParametros(nome);
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

	/**
	 * Método que verifica se um grupo editado sofreu modificações que não
	 * deveriam.
	 * 
	 * @param aghCapituloCid
	 * @throws ApplicationBusinessException
	 */
	private boolean valoresModificados(AghGrupoCids aghGrupoCid) {
		Integer seqAnterior = obterSeqAnterior(aghGrupoCid.getId().getSeq(), aghGrupoCid.getId().getCpcSeq());
		Integer cpcSeqAnterior = obterCpcSeqAnterior(aghGrupoCid.getId().getSeq(), aghGrupoCid.getId().getCpcSeq());
		String descricaoAnterior = obterDescricaoAnterior(aghGrupoCid.getId().getSeq(), aghGrupoCid.getId().getCpcSeq());
		Date dataCriacaoAnterior = obterDataCriacaoAnterior(aghGrupoCid.getId().getSeq(), aghGrupoCid.getId().getCpcSeq());
		Integer matriculaServidorAnterior = obterMatriculaServidorAnterior(aghGrupoCid.getId().getSeq(), aghGrupoCid.getId()
				.getCpcSeq());
		Short vinCodigoServidorAnterior = obterVinCodigoServidorAnterior(aghGrupoCid.getId().getSeq(), aghGrupoCid.getId().getCpcSeq());

		if (verificarDiferencaObject(aghGrupoCid.getId().getSeq(), seqAnterior)) {
			return true;
		}
		if (verificarDiferencaObject(aghGrupoCid.getId().getCpcSeq(), cpcSeqAnterior)) {
			return true;
		}
		if (verificarDiferencaObject(aghGrupoCid.getDescricao(), descricaoAnterior)) {
			return true;
		}
		if (verificarDiferencaDatas(aghGrupoCid.getCriadoEm(), dataCriacaoAnterior)) {
			return true;
		}
		if (verificarDiferencaObject(aghGrupoCid.getRapServidor().getId().getMatricula(), matriculaServidorAnterior)
				|| verificarDiferencaObject(aghGrupoCid.getRapServidor().getId().getVinCodigo(), vinCodigoServidorAnterior)) {
			return true;
		}
		return false;
	}

	/**
	 * Método para fazer comparação entre valores atuais e anteriores para
	 * atributos do objeto AghGrupoCids, evitando nullpointer.
	 * 
	 * @param vlrAnterior
	 * @param vlrAtual
	 * @return FALSE se parametros recebido forem iguais / TRUE se parametros
	 *         recebidos forem diferentes
	 */
	private Boolean verificarDiferencaObject(Object vlrAtual, Object vlrAnterior) {

		if (vlrAnterior == null && vlrAtual == null) {
			return false;
		} else if (vlrAnterior != null && !(vlrAnterior.equals(vlrAtual))) {
			return true;
		} else if (vlrAtual != null && !(vlrAtual.equals(vlrAnterior))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * TODO Este método deverá ficar centralizado Método para fazer comparação
	 * entre valores atuais e anteriores para atributos do tipo Data, evitando
	 * nullpointer.
	 * 
	 * @param data1
	 * @param data2
	 * @return FALSE se parametros recebido forem iguais / TRUE se parametros
	 *         recebidos forem diferentes
	 */
	public boolean verificarDiferencaDatas(Date data1, Date data2) {
		if (data1 == null && data2 == null) {
			return false;
		} else if (data1 == null && data2 != null) {
			return true;
		} else if (data2 == null && data1 != null) {
			return true;
		} else if (data1.compareTo(data2) != 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Método que obtem o seq do grupo salvo no banco
	 * 
	 * @param seq
	 *            , cpcSeq
	 * @return
	 */
	private Integer obterSeqAnterior(Integer seq, Integer cpcSeq) {
		return this.getAghuFacade().obterSeqAnterior(seq, cpcSeq);
	}

	/**
	 * Método que obtem o cpcSeq do grupo salvo no banco
	 * 
	 * @param seq
	 *            , cpcSeq
	 * @return
	 */
	private Integer obterCpcSeqAnterior(Integer seq, Integer cpcSeq) {
		return this.getAghuFacade().obterCpcSeqAnterior(seq, cpcSeq);
	}

	/**
	 * Método que obtem a descrição do grupo salvo no banco
	 * 
	 * @param seq
	 *            , cpcSeq
	 * @return
	 */
	private String obterDescricaoAnterior(Integer seq, Integer cpcSeq) {
		return this.getAghuFacade().obterDescricaoAnterior(seq, cpcSeq);
	}

	/**
	 * Método que obtem a data de criação do grupo salvo no banco
	 * 
	 * @param seq
	 *            , cpcSeq
	 * @return
	 */
	private Date obterDataCriacaoAnterior(Integer seq, Integer cpcSeq) {
		return this.getAghuFacade().obterDataCriacaoAnterior(seq, cpcSeq);
	}

	/**
	 * Método que obtem o servidor do grupo salvo no banco
	 * 
	 * @param seq
	 *            , cpcSeq
	 * @return
	 */
	private Integer obterMatriculaServidorAnterior(Integer seq, Integer cpcSeq) {
		return this.getAghuFacade().obterMatriculaServidorAnterior(seq, cpcSeq);
	}

	/**
	 * Método que obtem o servidor do grupo salvo no banco
	 * 
	 * @param seq
	 *            , cpcSeq
	 * @return
	 */
	private Short obterVinCodigoServidorAnterior(Integer seq, Integer cpcSeq) {
		return this.getAghuFacade().obterVinCodigoServidorAnterior(seq, cpcSeq);
	}

	/**
	 * Método para buscar os registros de AghGrupoCids através do seq do
	 * AghCapituloCid.
	 * 
	 * @param seqGrupoCid
	 * @return
	 */
	public List<AghGrupoCids> pesquisarGrupoCidPorCapituloCid(Integer seqCapituloCid) {
		return this.getAghuFacade().pesquisarGrupoCidPorCapituloCid(seqCapituloCid);
	}

	public List<AghGrupoCids> listarPorSigla(Object paramPesquisa) {
		return pesquisarGrupoCidSIGLAS((String) paramPesquisa);
	}

	public List<AghGrupoCids> pesquisarGrupoCidSIGLA(String sigla) {
		return this.getAghuFacade().pesquisarGrupoCidSIGLA(sigla);
	}

	public List<AghGrupoCids> pesquisarGrupoCidSIGLAS(String sigla) {
		return this.getAghuFacade().pesquisarGrupoCidSIGLAS(sigla);
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}
	
	protected AghGrupoCidsDAO getAghGrupoCidsDAO() {
		return aghGrupoCidsDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
