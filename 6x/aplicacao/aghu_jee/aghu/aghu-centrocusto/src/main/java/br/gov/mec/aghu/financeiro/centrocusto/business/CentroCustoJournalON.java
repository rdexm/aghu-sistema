package br.gov.mec.aghu.financeiro.centrocusto.business;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.centrocusto.dao.FccCentroCustosDAO;
import br.gov.mec.aghu.centrocusto.dao.FccCentroCustosJnDAO;
import br.gov.mec.aghu.dominio.DominioArea;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoDespesa;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FccCentroCustosJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class CentroCustoJournalON extends BaseBusiness {

	@EJB
	private CentroCustoON centroCustoON;
	
	private static final Log LOG = LogFactory.getLog(CentroCustoJournalON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private FccCentroCustosJnDAO fccCentroCustosJnDAO;
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private FccCentroCustosDAO fccCentroCustosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6653608432785696160L;

	/**
	 * Método que conduz a geração de uma entrada na tabela de journal na
	 * inserção/atualização de um centro de custos.
	 * 
	 * @param paciente
	 */
	public void observarPersistenciaCentroCusto(FccCentroCustos centroCusto,
			DominioOperacoesJournal operacaoJournal) {
		
		if (operacaoJournal.equals(DominioOperacoesJournal.INS)) {
			comitaAlteracoesCentroCustos(centroCusto);
			this.gerarJournalPersistenciaCentroCusto(centroCusto, null,
					operacaoJournal);
		} else {
			Map<FccCentroCustos.Fields, Object> valoresAnteriores = this
					.obterValoresAnterioresCentroCusto(centroCusto.getCodigo());
			
			comitaAlteracoesCentroCustos(centroCusto);
			// comita apos ter pesquisado os dados anteriores

			if (this.validarGeracaoJournalPersistenciaCentroCusto(centroCusto,
					valoresAnteriores)) {
				this.gerarJournalPersistenciaCentroCusto(centroCusto,
						valoresAnteriores, operacaoJournal);
			}
		}

		getFccCentroCustosJnDAO().flush(); // comita inserção do journal
	}

	/**
	 * @param centroCusto
	 */
	private void comitaAlteracoesCentroCustos(FccCentroCustos centroCusto) {
		FccCentroCustosDAO fccCentroCustosDAO = getFccCentroCustosDAO();

		if (!fccCentroCustosDAO.contains(centroCusto)) {
			fccCentroCustosDAO.merge(centroCusto);
		}
		fccCentroCustosDAO.flush();
	}

	/**
	 * Método que obtem os valores anteriores de certos atributos do pacientes,
	 * a serem usados no processo de geração do journal. Por anteriores
	 * entenda-se anterior a possível alteração do usuário na interface do
	 * sistema. Este método fura a cache de primeiro nível do hibernate!
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	private Map<FccCentroCustos.Fields, Object> obterValoresAnterioresCentroCusto(
			Integer codigo) {

		Map<FccCentroCustos.Fields, Object> resultado = new HashMap<FccCentroCustos.Fields, Object>();

		Object[] retornoConsulta = getFccCentroCustosJnDAO().obterValoresAnterioresCentroCusto(codigo);

		if (retornoConsulta == null) {
			return null;
		}

		resultado.put(FccCentroCustos.Fields.CODIGO, retornoConsulta[0]);
		resultado.put(FccCentroCustos.Fields.DESCRICAO, retornoConsulta[1]);
		resultado.put(FccCentroCustos.Fields.GRUPO_CC_CODIGO,
				retornoConsulta[2]);
		resultado.put(FccCentroCustos.Fields.GRUPO_CC_SEQ, retornoConsulta[3]);
		resultado.put(FccCentroCustos.Fields.AREA, retornoConsulta[4]);
		resultado.put(FccCentroCustos.Fields.PESO, retornoConsulta[5]);
		resultado.put(FccCentroCustos.Fields.ABSENTEISMO_SMO,
				retornoConsulta[6]);
		resultado.put(FccCentroCustos.Fields.CC_SUPERIOR, retornoConsulta[7]);
		resultado.put(FccCentroCustos.Fields.SOLICITA_COMPRA,
				retornoConsulta[8]);
		resultado.put(FccCentroCustos.Fields.AVALIACAO_TECNICA,
				retornoConsulta[9]);
		resultado.put(FccCentroCustos.Fields.APROVA_SOLICITACAO,
				retornoConsulta[10]);
		resultado.put(FccCentroCustos.Fields.CC_RH, retornoConsulta[11]);
		resultado.put(FccCentroCustos.Fields.NRO_VAGAS, retornoConsulta[12]);
		resultado.put(FccCentroCustos.Fields.SERVIDOR_MATRICULA,
				retornoConsulta[13]);
		resultado.put(FccCentroCustos.Fields.SERVIDOR_VINCULO,
				retornoConsulta[14]);
		resultado.put(FccCentroCustos.Fields.COTA_HORA_EXTRA,
				retornoConsulta[15]);
		resultado
				.put(FccCentroCustos.Fields.NOME_REDUZIDO, retornoConsulta[16]);
		resultado.put(FccCentroCustos.Fields.ORGANOGRAMA_CF,
				retornoConsulta[17]);
		resultado.put(FccCentroCustos.Fields.REGISTRO_FUNCIONAMENTO,
				retornoConsulta[18]);
		resultado.put(FccCentroCustos.Fields.LOGRADOURO, retornoConsulta[19]);
		resultado.put(FccCentroCustos.Fields.NRO_LOGRADOURO,
				retornoConsulta[20]);
		resultado.put(FccCentroCustos.Fields.COMPLEMENTO_LOGRADOURO,
				retornoConsulta[21]);
		resultado.put(FccCentroCustos.Fields.CEP, retornoConsulta[22]);
		resultado.put(FccCentroCustos.Fields.BAIRRO, retornoConsulta[23]);
		resultado.put(FccCentroCustos.Fields.CIDADE_CODIGO, retornoConsulta[24]);
		resultado.put(FccCentroCustos.Fields.DDD_FONE, retornoConsulta[25]);
		resultado.put(FccCentroCustos.Fields.FONE, retornoConsulta[26]);
		resultado.put(FccCentroCustos.Fields.DDD_FAX, retornoConsulta[27]);
		resultado.put(FccCentroCustos.Fields.FAX, retornoConsulta[28]);
		resultado.put(FccCentroCustos.Fields.CAIXA_POSTAL, retornoConsulta[29]);
		resultado.put(FccCentroCustos.Fields.EMAIL, retornoConsulta[30]);
		resultado.put(FccCentroCustos.Fields.HOME_PAGE, retornoConsulta[31]);
		resultado.put(FccCentroCustos.Fields.TIPO_DESPESA, retornoConsulta[32]);
		resultado.put(FccCentroCustos.Fields.SITUACAO, retornoConsulta[33]);
		resultado.put(FccCentroCustos.Fields.IND_AREA, retornoConsulta[34]);

		return resultado;
	}

	/**
	 * Método responsável por avaliar a necessidade de geração de uma entrada na
	 * tabela de journal para uma atualização de um paciente. Retorna true caso
	 * seja necessário gerar o journal.
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private Boolean validarGeracaoJournalPersistenciaCentroCusto(
			FccCentroCustos centroCusto,
			Map<FccCentroCustos.Fields, Object> valoresAnteriores) {

		if (centroCusto == null) {
			return false;
		}
		
		if(valoresAnteriores == null){
			return true;
		}

		Integer codigoAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.CODIGO) == null ? null
				: (Integer) valoresAnteriores
						.get(FccCentroCustos.Fields.CODIGO);
		String descricaoAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.DESCRICAO) == null ? null
				: (String) valoresAnteriores
						.get(FccCentroCustos.Fields.DESCRICAO);
		DominioSituacao situacaoAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.SITUACAO) == null ? null
				: (DominioSituacao) valoresAnteriores
						.get(FccCentroCustos.Fields.SITUACAO);
		Short grupoCCCodigoAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.GRUPO_CC_CODIGO) == null ? null
				: (Short) valoresAnteriores
						.get(FccCentroCustos.Fields.GRUPO_CC_CODIGO);
		Short grupoCCSeqAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.GRUPO_CC_SEQ) == null ? null
				: (Short) valoresAnteriores
						.get(FccCentroCustos.Fields.GRUPO_CC_SEQ);
		Short pesoAnterior = valoresAnteriores.get(FccCentroCustos.Fields.PESO) == null ? null
				: (Short) valoresAnteriores.get(FccCentroCustos.Fields.PESO);
		Boolean absentismoSmoAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.ABSENTEISMO_SMO) == null ? null
				: (Boolean) valoresAnteriores
						.get(FccCentroCustos.Fields.ABSENTEISMO_SMO);
		Integer ccSuperiorAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.CC_SUPERIOR) == null ? null
				: (Integer) valoresAnteriores
						.get(FccCentroCustos.Fields.CC_SUPERIOR);
		Boolean solicitaCompraAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.SOLICITA_COMPRA) == null ? null
				: (Boolean) valoresAnteriores
						.get(FccCentroCustos.Fields.SOLICITA_COMPRA);
		Boolean avaliacaoTecnicaAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.AVALIACAO_TECNICA) == null ? null
				: (Boolean) valoresAnteriores
						.get(FccCentroCustos.Fields.AVALIACAO_TECNICA);
		Boolean aprovaSolicitacaoAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.APROVA_SOLICITACAO) == null ? null
				: (Boolean) valoresAnteriores
						.get(FccCentroCustos.Fields.APROVA_SOLICITACAO);
		String ccRhAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.CC_RH) == null ? null
				: (String) valoresAnteriores.get(FccCentroCustos.Fields.CC_RH);
		Short nroVagasAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.NRO_VAGAS) == null ? null
				: (Short) valoresAnteriores
						.get(FccCentroCustos.Fields.NRO_VAGAS);
		Integer servidorMatriculaAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.SERVIDOR_MATRICULA) == null ? null
				: (Integer) valoresAnteriores
						.get(FccCentroCustos.Fields.SERVIDOR_MATRICULA);
		Short servidorVinculoAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.SERVIDOR_VINCULO) == null ? null
				: (Short) valoresAnteriores
						.get(FccCentroCustos.Fields.SERVIDOR_VINCULO);
		Integer cotaHoraExtraAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.COTA_HORA_EXTRA) == null ? null
				: (Integer) valoresAnteriores
						.get(FccCentroCustos.Fields.COTA_HORA_EXTRA);
		String nomeReduzidoAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.NOME_REDUZIDO) == null ? null
				: (String) valoresAnteriores
						.get(FccCentroCustos.Fields.NOME_REDUZIDO);
		String organogramaAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.ORGANOGRAMA_CF) == null ? null
				: (String) valoresAnteriores
						.get(FccCentroCustos.Fields.ORGANOGRAMA_CF);
		String registroFuncionamentoAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.REGISTRO_FUNCIONAMENTO) == null ? null
				: (String) valoresAnteriores
						.get(FccCentroCustos.Fields.REGISTRO_FUNCIONAMENTO);
		String logradouroAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.LOGRADOURO) == null ? null
				: (String) valoresAnteriores
						.get(FccCentroCustos.Fields.LOGRADOURO);
		Integer numeroLogradouroAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.NRO_LOGRADOURO) == null ? null
				: (Integer) valoresAnteriores
						.get(FccCentroCustos.Fields.NRO_LOGRADOURO);
		String complementoLogradouroAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.COMPLEMENTO_LOGRADOURO) == null ? null
				: (String) valoresAnteriores
						.get(FccCentroCustos.Fields.COMPLEMENTO_LOGRADOURO);
		Integer cepAnterior = valoresAnteriores.get(FccCentroCustos.Fields.CEP) == null ? null
				: (Integer) valoresAnteriores.get(FccCentroCustos.Fields.CEP);
		String bairroAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.BAIRRO) == null ? null
				: (String) valoresAnteriores.get(FccCentroCustos.Fields.BAIRRO);
		Integer codigoCidadeAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.CIDADE_CODIGO) == null ? null
				: (Integer) valoresAnteriores
						.get(FccCentroCustos.Fields.CIDADE_CODIGO);
		Short dddFoneAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.DDD_FONE) == null ? null
				: (Short) valoresAnteriores
						.get(FccCentroCustos.Fields.DDD_FONE);
		Long foneAnterior = valoresAnteriores.get(FccCentroCustos.Fields.FONE) == null ? null
				: (Long) valoresAnteriores.get(FccCentroCustos.Fields.FONE);
		Short dddFaxAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.DDD_FAX) == null ? null
				: (Short) valoresAnteriores.get(FccCentroCustos.Fields.DDD_FAX);
		Long faxAnterior = valoresAnteriores.get(FccCentroCustos.Fields.FAX) == null ? null
				: (Long) valoresAnteriores.get(FccCentroCustos.Fields.FAX);
		String caixaPostalAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.CAIXA_POSTAL) == null ? null
				: (String) valoresAnteriores
						.get(FccCentroCustos.Fields.CAIXA_POSTAL);
		String emailAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.EMAIL) == null ? null
				: (String) valoresAnteriores.get(FccCentroCustos.Fields.EMAIL);
		String homePageAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.HOME_PAGE) == null ? null
				: (String) valoresAnteriores
						.get(FccCentroCustos.Fields.HOME_PAGE);
		DominioTipoDespesa tipoDespesaAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.TIPO_DESPESA) == null ? null
				: (DominioTipoDespesa) valoresAnteriores
						.get(FccCentroCustos.Fields.TIPO_DESPESA);
		BigDecimal areaAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.AREA) == null ? null
				: (BigDecimal) valoresAnteriores
						.get(FccCentroCustos.Fields.AREA);
		DominioArea indAreaAnterior = valoresAnteriores
				.get(FccCentroCustos.Fields.IND_AREA) == null ? null
				: (DominioArea) valoresAnteriores
						.get(FccCentroCustos.Fields.IND_AREA);

		if (this.verificarDiferencaObject(centroCusto.getCodigo(),
				codigoAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getDescricao(),
				descricaoAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getIndSituacao(),
				situacaoAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getGccCodigo(),
				grupoCCCodigoAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(
				centroCusto.getGrupoCentroCusto() == null ? null : centroCusto
						.getGrupoCentroCusto().getSeq(), grupoCCSeqAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getPeso(), pesoAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getIndAbsentSmo(),
				absentismoSmoAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(
				centroCusto.getCentroCusto() == null ? null : centroCusto
						.getCentroCusto().getCodigo(), ccSuperiorAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getIndSolicitaCompra(),
				solicitaCompraAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getIndAvaliacaoTecnica(),
				avaliacaoTecnicaAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(
				centroCusto.getIndAprovaSolicitacao(),
				aprovaSolicitacaoAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getCcustRh(),
				ccRhAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getNroVagas(),
				nroVagasAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(
				centroCusto.getRapServidor() == null ? null : centroCusto
						.getRapServidor().getId().getMatricula(),
				servidorMatriculaAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(
				centroCusto.getRapServidor() == null ? null : centroCusto
						.getRapServidor().getId().getVinCodigo(),
				servidorVinculoAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getCotaHoraExtra(),
				cotaHoraExtraAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getNomeReduzido(),
				nomeReduzidoAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getOrganogramaCf(),
				organogramaAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto
				.getRegistroFuncionamento(), registroFuncionamentoAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getLogradouro(),
				logradouroAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getNroLogradouro(),
				numeroLogradouroAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getComplLogradouro(),
				complementoLogradouroAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getCep(), cepAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getBairro(),
				bairroAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(
				centroCusto.getCidade() == null ? null : centroCusto
						.getCidade().getCodigo(), codigoCidadeAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getDddFone(),
				dddFoneAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getFone(), foneAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getDddFax(),
				dddFaxAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getFax(), faxAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getCaixaPostal(),
				caixaPostalAnterior)) {
			return true;
		}

		if (this
				.verificarDiferencaObject(centroCusto.getEmail(), emailAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getHomePage(),
				homePageAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getIndTipoDespesa(),
				tipoDespesaAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getArea(), areaAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(centroCusto.getIndArea(),
				indAreaAnterior)) {
			return true;
		}

		return false;
	}

	/**
	 * Método para fazer comparação entre valores atuais e anteriores para
	 * atributos do objeto FccCentroCustos, evitando nullpointer.
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
	 * Método responsável pela agregação e persistencia do objeto
	 * FccCentroCustosJn, que representa o journal do centro de custos. Esse
	 * método gera o registro de journal na inserção e atualiação de um objeto
	 * de FccCentroCustos.
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private void gerarJournalPersistenciaCentroCusto(
			FccCentroCustos centroCusto,
			Map<FccCentroCustos.Fields, Object> valoresAnteriores,
			DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		FccCentroCustosJn jn = BaseJournalFactory.getBaseJournal(operacao, FccCentroCustosJn.class, servidorLogado.getUsuario());
		CentroCustoON centroCustoON = getCentroCustoON();
		
		if (DominioOperacoesJournal.INS.equals(operacao)) {

			jn.setCodigo(centroCusto.getCodigo());
			jn.setDescricao(centroCusto.getDescricao());
			jn.setIndSituacao(centroCusto.getIndSituacao());

			if (centroCusto.getGrupoCentroCusto() != null) {
				jn.setFcuGrupoCentroCusto(centroCustoON
						.obterGrupoCentroCusto(centroCusto
								.getGrupoCentroCusto().getSeq()));
			}

			jn.setPeso(centroCusto.getPeso());

			if (centroCusto.getIndAbsentSmo()) {
				jn.setIndAbsentSmo(DominioSimNao.S);
			} else {
				jn.setIndAbsentSmo(DominioSimNao.N);
			}

			if (centroCusto.getCentroCusto() != null) {
				jn.setCentroCusto(centroCustoON
						.obterFccCentroCustos(centroCusto.getCentroCusto()
								.getCodigo()));
			}

			if (centroCusto.getIndSolicitaCompra()) {
				jn.setIndSolicitaCompra(DominioSimNao.S);
			} else {
				jn.setIndSolicitaCompra(DominioSimNao.N);
			}

			if (centroCusto.getIndAvaliacaoTecnica()) {
				jn.setIndAvaliacaoTecnica(DominioSimNao.S);
			} else {
				jn.setIndAvaliacaoTecnica(DominioSimNao.N);
			}

			if (centroCusto.getIndAprovaSolicitacao()) {
				jn.setIndAprovaSolicitacao(DominioSimNao.S);
			} else {
				jn.setIndAprovaSolicitacao(DominioSimNao.N);
			}

			jn.setCcustRh(centroCusto.getCcustRh());
			jn.setNroVagas(centroCusto.getNroVagas());

			if (centroCusto.getRapServidor() != null) {
				RapServidoresId servidorId = new RapServidoresId();
				servidorId.setMatricula(centroCusto.getRapServidor().getId()
						.getMatricula());
				servidorId.setVinCodigo(centroCusto.getRapServidor().getId()
						.getVinCodigo());
				RapServidores servidor = new RapServidores();
				servidor.setId(servidorId);
				jn.setServidor(getRegistroColaboradorFacade()
						.obterServidor(servidor));
			}

			jn.setCotaHoraExtra(centroCusto.getCotaHoraExtra());
			jn.setIndArea(centroCusto.getIndArea());
			jn.setArea(centroCusto.getArea());
			jn.setNomeReduzido(centroCusto.getNomeReduzido());

			jn.setOrganogramaCf(centroCusto.getOrganogramaCf());
			jn.setRegistroFuncionamento(centroCusto
					.getRegistroFuncionamento());
			jn.setLogradouro(centroCusto.getLogradouro());
			jn.setNroLogradouro(centroCusto.getNroLogradouro());
			jn.setComplLogradouro(centroCusto
					.getComplLogradouro());
			jn.setCep(centroCusto.getCep());
			jn.setBairro(centroCusto.getBairro());

			if (centroCusto.getCidade() != null) {
				jn.setCidade(getCadastrosBasicosPacienteFacade().obterCidadePorCodigo(
						centroCusto.getCidade().getCodigo(), false));
			}

			jn.setDddFone(centroCusto.getDddFone());
			jn.setFone(centroCusto.getFone());
			jn.setDddFax(centroCusto.getDddFax());
			jn.setFax(centroCusto.getFax());
			jn.setCaixaPostal(centroCusto.getCaixaPostal());
			jn.setEmail(centroCusto.getEmail());
			jn.setHomePage(centroCusto.getHomePage());
			jn.setIndTipoDespesa(centroCusto
					.getIndTipoDespesa());

		} else {
			Integer codigoAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.CODIGO) == null ? null
					: (Integer) valoresAnteriores
							.get(FccCentroCustos.Fields.CODIGO);
			String descricaoAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.DESCRICAO) == null ? null
					: (String) valoresAnteriores
							.get(FccCentroCustos.Fields.DESCRICAO);
			DominioSituacao situacaoAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.SITUACAO) == null ? null
					: (DominioSituacao) valoresAnteriores
							.get(FccCentroCustos.Fields.SITUACAO);
			// O campo abaixo não é mais usado (o correto é o
			// grupoCCSeqAnterior, referente ao FCU_GRUPO_CENTRO_CUSTOS)
			// Short grupoCCCodigoAnterior =
			// valoresAnteriores.get(FccCentroCustos.Fields.GRUPO_CC_CODIGO) ==
			// null ? null
			// : (Short)
			// valoresAnteriores.get(FccCentroCustos.Fields.GRUPO_CC_CODIGO);
			Short grupoCCSeqAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.GRUPO_CC_SEQ) == null ? null
					: (Short) valoresAnteriores
							.get(FccCentroCustos.Fields.GRUPO_CC_SEQ);
			Short pesoAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.PESO) == null ? null
					: (Short) valoresAnteriores
							.get(FccCentroCustos.Fields.PESO);
			Boolean absentismoSmoAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.ABSENTEISMO_SMO) == null ? null
					: (Boolean) valoresAnteriores
							.get(FccCentroCustos.Fields.ABSENTEISMO_SMO);
			Integer ccSuperiorAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.CC_SUPERIOR) == null ? null
					: (Integer) valoresAnteriores
							.get(FccCentroCustos.Fields.CC_SUPERIOR);
			Boolean solicitaCompraAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.SOLICITA_COMPRA) == null ? null
					: (Boolean) valoresAnteriores
							.get(FccCentroCustos.Fields.SOLICITA_COMPRA);
			Boolean avaliacaoTecnicaAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.AVALIACAO_TECNICA) == null ? null
					: (Boolean) valoresAnteriores
							.get(FccCentroCustos.Fields.AVALIACAO_TECNICA);
			Boolean aprovaSolicitacaoAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.APROVA_SOLICITACAO) == null ? null
					: (Boolean) valoresAnteriores
							.get(FccCentroCustos.Fields.APROVA_SOLICITACAO);
			String ccRhAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.CC_RH) == null ? null
					: (String) valoresAnteriores
							.get(FccCentroCustos.Fields.CC_RH);
			Short nroVagasAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.NRO_VAGAS) == null ? null
					: (Short) valoresAnteriores
							.get(FccCentroCustos.Fields.NRO_VAGAS);
			Integer servidorMatriculaAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.SERVIDOR_MATRICULA) == null ? null
					: (Integer) valoresAnteriores
							.get(FccCentroCustos.Fields.SERVIDOR_MATRICULA);
			Short servidorVinculoAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.SERVIDOR_VINCULO) == null ? null
					: (Short) valoresAnteriores
							.get(FccCentroCustos.Fields.SERVIDOR_VINCULO);
			Integer cotaHoraExtraAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.COTA_HORA_EXTRA) == null ? null
					: (Integer) valoresAnteriores
							.get(FccCentroCustos.Fields.COTA_HORA_EXTRA);
			String nomeReduzidoAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.NOME_REDUZIDO) == null ? null
					: (String) valoresAnteriores
							.get(FccCentroCustos.Fields.NOME_REDUZIDO);
			String organogramaAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.ORGANOGRAMA_CF) == null ? null
					: (String) valoresAnteriores
							.get(FccCentroCustos.Fields.ORGANOGRAMA_CF);
			String registroFuncionamentoAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.REGISTRO_FUNCIONAMENTO) == null ? null
					: (String) valoresAnteriores
							.get(FccCentroCustos.Fields.REGISTRO_FUNCIONAMENTO);
			String logradouroAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.LOGRADOURO) == null ? null
					: (String) valoresAnteriores
							.get(FccCentroCustos.Fields.LOGRADOURO);
			Integer numeroLogradouroAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.NRO_LOGRADOURO) == null ? null
					: (Integer) valoresAnteriores
							.get(FccCentroCustos.Fields.NRO_LOGRADOURO);
			String complementoLogradouroAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.COMPLEMENTO_LOGRADOURO) == null ? null
					: (String) valoresAnteriores
							.get(FccCentroCustos.Fields.COMPLEMENTO_LOGRADOURO);
			Integer cepAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.CEP) == null ? null
					: (Integer) valoresAnteriores
							.get(FccCentroCustos.Fields.CEP);
			String bairroAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.BAIRRO) == null ? null
					: (String) valoresAnteriores
							.get(FccCentroCustos.Fields.BAIRRO);
			Integer codigoCidadeAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.CIDADE_CODIGO) == null ? null
					: (Integer) valoresAnteriores
							.get(FccCentroCustos.Fields.CIDADE_CODIGO);
			Short dddFoneAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.DDD_FONE) == null ? null
					: (Short) valoresAnteriores
							.get(FccCentroCustos.Fields.DDD_FONE);
			Long foneAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.FONE) == null ? null
					: (Long) valoresAnteriores.get(FccCentroCustos.Fields.FONE);
			Short dddFaxAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.DDD_FAX) == null ? null
					: (Short) valoresAnteriores
							.get(FccCentroCustos.Fields.DDD_FAX);
			Long faxAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.FAX) == null ? null
					: (Long) valoresAnteriores.get(FccCentroCustos.Fields.FAX);
			String caixaPostalAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.CAIXA_POSTAL) == null ? null
					: (String) valoresAnteriores
							.get(FccCentroCustos.Fields.CAIXA_POSTAL);
			String emailAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.EMAIL) == null ? null
					: (String) valoresAnteriores
							.get(FccCentroCustos.Fields.EMAIL);
			String homePageAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.HOME_PAGE) == null ? null
					: (String) valoresAnteriores
							.get(FccCentroCustos.Fields.HOME_PAGE);
			DominioTipoDespesa tipoDespesaAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.TIPO_DESPESA) == null ? null
					: (DominioTipoDespesa) valoresAnteriores
							.get(FccCentroCustos.Fields.TIPO_DESPESA);
			BigDecimal areaAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.AREA) == null ? null
					: (BigDecimal) valoresAnteriores
							.get(FccCentroCustos.Fields.AREA);
			DominioArea indAreaAnterior = valoresAnteriores
					.get(FccCentroCustos.Fields.IND_AREA) == null ? null
					: (DominioArea) valoresAnteriores
							.get(FccCentroCustos.Fields.IND_AREA);

			jn.setCodigo(codigoAnterior);
			jn.setDescricao(descricaoAnterior);
			jn.setIndSituacao(situacaoAnterior);

			if (grupoCCSeqAnterior != null) {
				jn.setFcuGrupoCentroCusto(centroCustoON
						.obterGrupoCentroCusto(grupoCCSeqAnterior));
			}

			jn.setPeso(pesoAnterior);
			if (absentismoSmoAnterior) {
				jn.setIndAbsentSmo(DominioSimNao.S);
			} else {
				jn.setIndAbsentSmo(DominioSimNao.N);
			}

			if (ccSuperiorAnterior != null) {
				jn.setCentroCusto(centroCustoON
						.obterFccCentroCustos(ccSuperiorAnterior));
			}

			if (solicitaCompraAnterior) {
				jn.setIndSolicitaCompra(DominioSimNao.S);
			} else {
				jn.setIndSolicitaCompra(DominioSimNao.N);

			}

			if (avaliacaoTecnicaAnterior) {
				jn.setIndAvaliacaoTecnica(DominioSimNao.S);
			} else {
				jn.setIndAvaliacaoTecnica(DominioSimNao.N);
			}

			if (aprovaSolicitacaoAnterior) {
				jn.setIndAprovaSolicitacao(DominioSimNao.S);
			} else {
				jn.setIndAprovaSolicitacao(DominioSimNao.N);
			}

			jn.setCcustRh(ccRhAnterior);
			jn.setNroVagas(nroVagasAnterior);

			if (centroCusto.getRapServidor() != null) {
				RapServidoresId servidorId = new RapServidoresId();
				servidorId.setMatricula(servidorMatriculaAnterior);
				servidorId.setVinCodigo(servidorVinculoAnterior);
				RapServidores servidor = new RapServidores();
				servidor.setId(servidorId);
				jn.setServidor(getRegistroColaboradorFacade()
						.obterServidor(servidor));
			}

			jn.setCotaHoraExtra(cotaHoraExtraAnterior);
			jn.setIndArea(indAreaAnterior);
			jn.setArea(areaAnterior);
			jn.setNomeReduzido(nomeReduzidoAnterior);

			jn.setOrganogramaCf(organogramaAnterior);
			jn
					.setRegistroFuncionamento(registroFuncionamentoAnterior);
			jn.setLogradouro(logradouroAnterior);
			jn.setNroLogradouro(numeroLogradouroAnterior);
			jn
					.setComplLogradouro(complementoLogradouroAnterior);
			jn.setCep(cepAnterior);
			jn.setBairro(bairroAnterior);

			if (codigoCidadeAnterior != null) {
				jn.setCidade(getCadastrosBasicosPacienteFacade().obterCidadePorCodigo(
						codigoCidadeAnterior, false));
			}

			jn.setDddFone(dddFoneAnterior);
			jn.setFone(foneAnterior);
			jn.setDddFax(dddFaxAnterior);
			jn.setFax(faxAnterior);
			jn.setCaixaPostal(caixaPostalAnterior);
			jn.setEmail(emailAnterior);
			jn.setHomePage(homePageAnterior);
			jn.setIndTipoDespesa(tipoDespesaAnterior);
		}

		getFccCentroCustosJnDAO().persistir(jn);
	}

	protected CentroCustoON getCentroCustoON() {
		return centroCustoON;
	}
	
	protected FccCentroCustosDAO getFccCentroCustosDAO() {
		return fccCentroCustosDAO;
	}
	
	protected FccCentroCustosJnDAO getFccCentroCustosJnDAO() {
		return fccCentroCustosJnDAO;
	}
	
	protected ICadastrosBasicosPacienteFacade getCadastrosBasicosPacienteFacade() {
		return cadastrosBasicosPacienteFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
