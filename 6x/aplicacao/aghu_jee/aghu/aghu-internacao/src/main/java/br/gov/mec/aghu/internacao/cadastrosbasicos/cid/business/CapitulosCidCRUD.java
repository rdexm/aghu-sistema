package br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghCapitulosCid;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de capitulos cid.
 */
@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class CapitulosCidCRUD extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(CapitulosCidCRUD.class);
	
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

	/**
	 * 
	 */
	private static final long serialVersionUID = -8386006343483430800L;

	/**
	 * Enumeracao com os codigos de mensagens de excecoes negociais do cadastro
	 * de capitulos cid.
	 * 
	 * Cada entrada nesta enumeracao deve corresponder a um chave no message
	 * bundle de capitulos de cid.
	 * 
	 * Porém se não houver uma este valor será enviado como mensagem de execeção
	 * sem localização alguma.
	 */
	private enum CapitulosCidCRUDExceptionCode implements BusinessExceptionCode {
		ERRO_EXCLUSAO_DEPENDENCIA_GRUPO_CID, NUMERO_CAPITULO_CID_EXISTENTE, ERRO_REMOVER_CAPITULO_CID, ERRO_PERSISTIR_CAPITULO_CID, VALORES_MODIFICADOS_INVALIDOS, ERRO_RECUPERACAO_PARAM_P_DIAS_PERM_DEL, REGISTRO_FORA_PERIODO_PERMITIDO_PARA_EXCLUSAO, AIP_USUARIO_NAO_CADASTRADO, CAPITULO_CID_NUMERO_INVALIDO, CAPITULO_CID_SECUNDARIO_INVALIDO, DESCRICAO_CAPITULO_CID_OBRIGATORIO;
	}

	public List<AghCapitulosCid> pesquisar(Integer firstResult, Integer maxResults, Short numero, String descricao,
			DominioSimNao indExigeCidSecundario, DominioSituacao indSituacao) {
		return this.getAghuFacade().pesquisarAghCapitulosCid(firstResult, maxResults, numero, descricao, indExigeCidSecundario, indSituacao);
	}

	public Long obterCapituloCidCount(Short numero, String descricao, DominioSimNao indExigeCidSecundario,
			DominioSituacao indSituacao) {
		return this.getAghuFacade().obterCapituloCidCount(numero, descricao, indExigeCidSecundario, indSituacao);
	}

	/**
	 * Método responsável pela persistência de um capitulo de cid.
	 * 
	 * @param capituloCid
	 * @throws ApplicationBusinessException
	 */
	public void persistirCapituloCid(AghCapitulosCid capituloCid)
			throws ApplicationBusinessException {

		//try {

			if (capituloCid.getSeq() == null) {
				// inclusão
				this.incluirCapituloCid(capituloCid);
			} else {
				// edição
				this.atualizarCapituloCid(capituloCid);
			}
	}

	/**
	 * Método responsável por incluir um novo capitulo cid.
	 * 
	 * @param capituloCid
	 * @throws ApplicationBusinessException
	 */
	private void incluirCapituloCid(AghCapitulosCid capituloCid)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		this.validarDadosCapituloCid(capituloCid);
		capituloCid.setRapServidor(servidorLogado);
		capituloCid.setCriadoEm(Calendar.getInstance().getTime());
		if(capituloCid.getDescricao()!=null){
			capituloCid.setDescricao(capituloCid.getDescricao().toUpperCase());
		}
		this.getAghuFacade().inserirAghCapitulosCid(capituloCid);
	}

	/**
	 * Método responsável pela atualização de um capitulo do cid.
	 * 
	 * @param capituloCid
	 * @throws ApplicationBusinessException
	 */
	private void atualizarCapituloCid(AghCapitulosCid capituloCid)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.validarDadosCapituloCid(capituloCid);
		this.validarAtualizacao(capituloCid);
		capituloCid.setRapServidor(servidorLogado);
		if(capituloCid.getDescricao()!=null){
			capituloCid.setDescricao(capituloCid.getDescricao().toUpperCase());
		}
		this.getAghuFacade().atualizarAghCapitulosCid(capituloCid);
	}

	public AghCapitulosCid obterCapituloCid(Integer seq) {
		return this.getAghuFacade().obterAghCapitulosCidPorChavePrimaria(seq);
	}

	/**
	 * Apaga um capitulo do cid do banco de dados.
	 * 
	 * @param capituloCidSeq
	 *            ID do capitulo do Cid a ser removido.
	 * @throws ApplicationBusinessException
	 */
	public void removerCapituloCid(Integer capituloCidSeq)
			throws ApplicationBusinessException {
		
		AghCapitulosCid capitulosCid = this.getAghuFacade().obterAghCapitulosCidPorChavePrimaria(capituloCidSeq);		
		this.validarRemocao(capitulosCid);
		this.getAghuFacade().removerAghCapitulosCid(capitulosCid);
	}

	private void validarAtualizacao(AghCapitulosCid capituloCid) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (this.valoresModificados(capituloCid, servidorLogado)) {
			throw new ApplicationBusinessException(
					CapitulosCidCRUDExceptionCode.VALORES_MODIFICADOS_INVALIDOS);
		}
	}

	/**
	 * Método que verifica se um capitulo editado sofreu modificações que não
	 * deveriam.
	 * 
	 * @param aghCapituloCid
	 * @throws ApplicationBusinessException
	 */
	private boolean valoresModificados(AghCapitulosCid capitulosCid,
			RapServidores servidor) {
		Integer seqAnterior = this.obterSeqAnterior(capitulosCid.getSeq());
		String descricaoAnterior = this.obterDescricaoAnterior(capitulosCid.getSeq());
		Date dataCriacaoAnterior = this.obterDataCriacaoAnterior(capitulosCid
				.getSeq());
		Integer matriculaServidorAnterior = this.obterMatriculaServidorAnterior(capitulosCid
				.getSeq());
		Short vinCodigoServidorAnterior = this.obterVinCodigoServidorAnterior(capitulosCid
				.getSeq());

		if (this.verificarDiferencaObject(capitulosCid.getSeq(), seqAnterior)) {
			return true;
		}
		if (this.verificarDiferencaObject(capitulosCid.getDescricao(),
				descricaoAnterior)) {
			return true;
		}
		if (this.verificarDiferencaObject(capitulosCid.getCriadoEm(),
				dataCriacaoAnterior)) {
			return true;
		}

		if (this.isServidorDiferente(servidor, matriculaServidorAnterior,
				vinCodigoServidorAnterior)) {
			return true;
		}
		return false;
	}

	private boolean isServidorDiferente(RapServidores servidorCorrente,
			Integer matriculaAnterior, Short vinculoAnterior) {
		if (this.verificarDiferencaObject(servidorCorrente.getId().getMatricula(),
				matriculaAnterior)
				|| this.verificarDiferencaObject(servidorCorrente.getId()
						.getVinCodigo(), vinculoAnterior)) {
			return true;
		}
		return false;
	}

	private Date obterDataCriacaoAnterior(Integer seq) {
		return this.getAghuFacade().obterDataCriacaoAnterior(seq);
	}

	private Integer obterSeqAnterior(Integer seq) {
		return this.getAghuFacade().obterSeqAnterior(seq);
	}

	private String obterDescricaoAnterior(Integer seq) {
		return this.getAghuFacade().obterDescricaoAnterior(seq);
	}

	/**
	 * Método para fazer comparação entre valores atuais e anteriores para
	 * atributos do objeto AghCapitulosCid, evitando nullpointer.
	 * 
	 * @param vlrAnterior
	 * @param vlrAtual
	 * @return FALSE se parametros recebido forem iguais / TRUE se parametros
	 *         recebidos forem diferentes
	 */
	private Boolean verificarDiferencaObject(Object vlrAtual, Object vlrAnterior) {

		if (vlrAnterior == null && vlrAtual == null) {
			return false;
		} else if (vlrAnterior != null && !vlrAnterior.equals(vlrAtual)) {
			return true;
		} else if (vlrAtual != null && !vlrAtual.equals(vlrAnterior)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Método responsável pelas validações de remoção de um capitulo CID.
	 * 
	 * @param aghCapituloCid
	 * @throws ApplicationBusinessException
	 */
	private void validarRemocao(AghCapitulosCid capitulosCid)
			throws ApplicationBusinessException {

		final String nome = "P_DIAS_PERM_DEL_AGH";
		BigDecimal vlrNumerico = this.obterValorNumericoAghParametros(nome);
		if (vlrNumerico == null) {
			throw new ApplicationBusinessException(
					CapitulosCidCRUDExceptionCode.ERRO_RECUPERACAO_PARAM_P_DIAS_PERM_DEL);
		}

		Long diferencaEmDias = this.diferencaEmDias(capitulosCid.getCriadoEm(),
				new Date());
		if (diferencaEmDias.intValue() > vlrNumerico.intValue()) {
			throw new ApplicationBusinessException(
					CapitulosCidCRUDExceptionCode.REGISTRO_FORA_PERIODO_PERMITIDO_PARA_EXCLUSAO);
		}
		
		if (this.getAghuFacade().pesquisarGruposCidsCount(capitulosCid, null, null, null, null) != 0){
			throw new ApplicationBusinessException(
					CapitulosCidCRUDExceptionCode.ERRO_EXCLUSAO_DEPENDENCIA_GRUPO_CID);
		}
		
		
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

	/**
	 * Método que calcula a diferença em dias entre duas datas.
	 * 
	 * @param data1
	 *            , data2
	 * @return
	 */
	private Long diferencaEmDias(Date data1, Date data2) {

		long diferenca = data2.getTime() - data1.getTime();
		final int milisegundos = 1000;
		final int segundos = 60;
		final int minutos = 60;
		final int horas = 24;
		return diferenca / (milisegundos * segundos * minutos * horas);
	}

	/**
	 * Método que obtem o servidor do grupo salvo no banco
	 * 
	 * @param seq
	 * @return
	 */
	private Integer obterMatriculaServidorAnterior(Integer seq) {
		return this.getAghuFacade().obterMatriculaServidorAnterior(seq);
	}

	/**
	 * Método que obtem o servidor do capitulo salvo no banco
	 * 
	 * @param seq
	 * @return
	 */
	private Short obterVinCodigoServidorAnterior(Integer seq) {
		return this.getAghuFacade().obterVinCodigoServidorAnterior(seq);
	}

	/**
	 * Método responsável pelas validações dos dados de capítulo CID. Método
	 * utilizado para inclusão e atualização de capítulo CID.
	 * 
	 * @param capituloCid
	 * @throws ApplicationBusinessException
	 */
	private void validarDadosCapituloCid(AghCapitulosCid capituloCid)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
	
		final int min = 1;
		final int max = 999;
		
		if (StringUtils.isBlank(capituloCid.getDescricao())) {
			throw new ApplicationBusinessException(
					CapitulosCidCRUDExceptionCode.DESCRICAO_CAPITULO_CID_OBRIGATORIO);
		}
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(
					CapitulosCidCRUDExceptionCode.AIP_USUARIO_NAO_CADASTRADO);
		}
		if (capituloCid.getIndSituacao() == null) {
			capituloCid.setIndSituacao(DominioSituacao.A);
		}
		if (capituloCid.getIndExigeCidSecundario() == null) {
			capituloCid.setIndExigeCidSecundario(DominioSimNao.N);
		}
		if (capituloCid.getNumero() < min || capituloCid.getNumero() > max) {
			throw new ApplicationBusinessException(
					CapitulosCidCRUDExceptionCode.CAPITULO_CID_NUMERO_INVALIDO);

		}
		if (capituloCid.getIndDiagPrincipal().equals(DominioSimNao.N)
				&& capituloCid.getIndExigeCidSecundario().equals(
						DominioSimNao.S)) {
			throw new ApplicationBusinessException(
					CapitulosCidCRUDExceptionCode.CAPITULO_CID_SECUNDARIO_INVALIDO);

		}

		if (capituloCid.getSeq() != null
				&& this.isServidorDiferente(servidorLogado, capituloCid
						.getRapServidor().getId().getMatricula(), capituloCid
						.getRapServidor().getId().getVinCodigo())) {
			throw new ApplicationBusinessException(
					CapitulosCidCRUDExceptionCode.VALORES_MODIFICADOS_INVALIDOS);
		}
				
		//valida se ja existe um capitulo cadastrado com o mesmo numero e, no caso de edição, que não seja ele mesmo
		AghCapitulosCid capituloCidAux = this.getAghuFacade()
				.obterAghCapitulosCidPorNumero(capituloCid.getNumero(), capituloCid.getSeq());
				
		//Se existir um capitulo_cid com o mesmo número E for inclusao (não tem seq) ou for edição de outro capitulo
		if (capituloCidAux != null) {
			throw new ApplicationBusinessException(
					CapitulosCidCRUDExceptionCode.NUMERO_CAPITULO_CID_EXISTENTE, capituloCid.getNumero());
		}
	}

	public List<AghCapitulosCid> pesquisarCapitulosCidsAtivo() {
		return this.getAghuFacade().pesquisarCapitulosCidsAtivo();
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
