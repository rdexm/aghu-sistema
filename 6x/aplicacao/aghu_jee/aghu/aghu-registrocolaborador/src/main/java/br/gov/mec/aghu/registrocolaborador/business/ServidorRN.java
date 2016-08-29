package br.gov.mec.aghu.registrocolaborador.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Years;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.dominio.DominioTipoRemuneracao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapCodStarhLivres;
import br.gov.mec.aghu.model.RapHistCCustoDesempenho;
import br.gov.mec.aghu.model.RapHistCCustoDesempenhoId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.RapServidoresJn;
import br.gov.mec.aghu.registrocolaborador.dao.RapAfastamentoDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapCodStarhLivresDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapHistCCustoDesempenhoDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresJnDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength" , "PMD.AtributoEmSeamContextManager"})
@Stateless
public class ServidorRN extends BaseBusiness {

	private static final String PARAMETRO_OBRIGATORIO = "Parâmetro obrigatório.";

	private static final Log LOG = LogFactory.getLog(ServidorRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private RapCodStarhLivresDAO rapCodStarhLivresDAO;
	
	@Inject
	private RapServidoresJnDAO rapServidoresJnDAO;
	
	@Inject
	private EmailUtil emailUtil;
	
	@Inject
	private RapServidoresDAO rapServidoresDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private RapHistCCustoDesempenhoDAO rapHistCCustoDesempenhoDAO;
	
	@Inject
	private RapAfastamentoDAO rapAfastamentoDAO;
	
	@Inject
	private ObjetosOracleDAO objetosOracleDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -381576631291548368L;
	private String alterouCCusto = "N";
	private boolean revogaFornecePerfis = false;
	private boolean ajustarNtEmail = false;
	private boolean ativarNtEmail  = false;

	public enum ServidorRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_VINCULO_INATIVO, MENSAGEM_INFORMAR_HORARIO, MENSAGEM_INFORMAR_OCUPACAO,
		MENSAGEM_DATA_INICIO_SUPERIOR, MENSAGEM_DATA_INICIO_POSTERIOR_DATA_ATUAL,
		MENSAGEM_DATA_FIM_ANTERIOR_DATA_INICIO, MENSAGEM_DATA_TERMINO_OBRIGATORIA,
		MENSAGEM_CENTRO_CUSTO_LOTACAO_INATIVO, MENSAGEM_CENTRO_CUSTO_ATUACAO_INATIVO,		
		MENSAGEM_CENTRO_CUSTO_SEM_ORGANOGRAMA, MENSAGEM_VINCULO_CONTROLADO_STARH,
		MENSAGEM_USUARIO_SEM_PERMISSAO_ALTERAR_SERVIDOR, MENSAGEM_GPPG_SEM_PERMISSAO_ALTERAR,
		MENSAGEM_INCLUSAO_VINCULO_CONTROLADO_STARH, MENSAGEM_PROBLEMA_GERACAO_MATRICULA,
		MENSAGEM_DATA_NASCIMENTO_SUPERIOR_DATA_ATUAL,
		MENSAGEM_EXIGE_CPF_RG, MENSAGEM_PESSOA_FISICA_OBRIGATORIO,
		MENSAGEM_VINCULO_OBRIGATORIO, MENSAGEM_CC_LOTACAO_OBRIGATORIO, MENSAGEM_INFORMAR_CCUSTO_LOTACAO,
		MENSAGEM_SERVIDOR_MATRICULA_NULA, SERVIDOR_ID_DUPLICADO;		
	}
	
	private enum Descricao {
		AFASTADO("Afastado"), INATIVO("Inativo");

		private String descricao;

		private Descricao(String descricao) {
			this.descricao = descricao;
		}

		public String toString() {
			return this.descricao;
		}
	}	
	
	/**
	 * ORADB: Procedure RAPK_SER_RN.RN_SERP_VER_VINCULO
	 * Logica de negocio duplicado no AghuBaseLoginModule.
	 * 
	 * @param servidor
	 * @throws ApplicationBusinessException
	 */
	public void verificarVinculoServidor(RapServidores servidor)
			throws ApplicationBusinessException {

		Calendar dataAtual = Calendar.getInstance();	
		dataAtual.setTime(DateUtil.obterDataComHoraInical(null));
		
		if (servidor == null || servidor.getVinculo() == null ) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO);
		}
		
		if (servidor.getVinculo().getIndSituacao() != DominioSituacao.A) {
			throw new ApplicationBusinessException(
					ServidorRNExceptionCode.MENSAGEM_VINCULO_INATIVO);

		}

		if (servidor.getVinculo().getIndCcustLotacao() == DominioSimNao.S
				&& servidor.getCentroCustoLotacao() == null) {
			throw new ApplicationBusinessException(
					ServidorRNExceptionCode.MENSAGEM_INFORMAR_CCUSTO_LOTACAO);
		}
		
		if (servidor.getVinculo().getIndHorario() == DominioSimNao.S
				&& servidor.getHorarioTrabalho() == null) {
			throw new ApplicationBusinessException(
					ServidorRNExceptionCode.MENSAGEM_INFORMAR_HORARIO);
		}

		if (servidor.getVinculo().getIndOcupacao() == DominioSimNao.S
				&& servidor.getOcupacaoCargo() == null) {
			throw new ApplicationBusinessException(
					ServidorRNExceptionCode.MENSAGEM_INFORMAR_OCUPACAO);
		}

		if (servidor.getDtInicioVinculo().after(dataAtual.getTime())) {

			Integer nroDiasAdmissao = servidor.getVinculo()
					.getNroDiasAdmissao();

			if (nroDiasAdmissao != null && nroDiasAdmissao > 0) {

				dataAtual.add(Calendar.DAY_OF_MONTH, nroDiasAdmissao);

				if (servidor.getDtInicioVinculo().after(dataAtual.getTime())) {
					throw new ApplicationBusinessException(
							ServidorRNExceptionCode.MENSAGEM_DATA_INICIO_SUPERIOR);
				}

			} else {
				throw new ApplicationBusinessException(
						ServidorRNExceptionCode.MENSAGEM_DATA_INICIO_POSTERIOR_DATA_ATUAL);
			}

		}

	}

	/**
	 * ORADB: Procedure RAPK_SER_RN.RN_SERP_VER_DT_INIC
	 * 
	 * @param dataInicioVinculo
	 * @param dataFimVinculo
	 * @throws ApplicationBusinessException
	 */
	private void verificarDataInicioVinculo(Date dataInicioVinculo,
			Date dataFimVinculo) throws ApplicationBusinessException {

		if (dataInicioVinculo != null && dataFimVinculo != null) {
			if (dataInicioVinculo.after(dataFimVinculo)) {
				throw new ApplicationBusinessException(
						ServidorRNExceptionCode.MENSAGEM_DATA_FIM_ANTERIOR_DATA_INICIO);
			}
		}

	}

	/**
	 * ORADB: Procedure RAPK_SER_RN.RN_SERP_ATU_SITUACAO
	 * 
	 * @param servidor
	 */
	private void atualizarSituacaoServidor(RapServidores servidor) {

		if (servidor == null) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO);
		}

		Date dataFimVinculo = servidor.getDtFimVinculo();
		Date dataAtual = DateUtil.obterDataComHoraInical(null);

		if (dataFimVinculo == null) {
			servidor.setIndSituacao(DominioSituacaoVinculo.A);
		} else if (dataFimVinculo.before(dataAtual)
				|| dataFimVinculo.equals(dataAtual)) {
			servidor.setIndSituacao(DominioSituacaoVinculo.I);
		} else if (dataFimVinculo.after(dataAtual)) {
			servidor.setIndSituacao(DominioSituacaoVinculo.P);
		}

	}

	/**
	 * ORADB: Procedure RAPK_SER_RN.RN_SERP_VER_TERMINO
	 * 
	 * @param indExigeTermino
	 * @param dataFimVinculo
	 * @throws ApplicationBusinessException
	 */
	private void verificarDataTerminoVinculo(DominioSimNao indExigeTermino,
			Date dataFimVinculo) throws ApplicationBusinessException {
		if (indExigeTermino == null) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO);
		}

		if (indExigeTermino == DominioSimNao.S && dataFimVinculo == null) {
			throw new ApplicationBusinessException(
					ServidorRNExceptionCode.MENSAGEM_DATA_TERMINO_OBRIGATORIA);
		}

	}

	/**
	 * ORADB: Procedure: RAPK_SER_RN.RN_SERP_VER_CC_LOT
	 * 
	 * @param situacaoCCLotacao
	 * @throws ApplicationBusinessException
	 */
	private void verificarSituacaoCCLotacao(DominioSituacao situacaoCCLotacao)
			throws ApplicationBusinessException {

		if (situacaoCCLotacao == null) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO);
		}

		if (situacaoCCLotacao != DominioSituacao.A) {
			throw new ApplicationBusinessException(
					ServidorRNExceptionCode.MENSAGEM_CENTRO_CUSTO_LOTACAO_INATIVO);
		}

	}

	/**
	 * ORADB: Procedure RAPK_SER_RN.RN_SERP_VER_CC_ATUA
	 * 
	 * @param situacaoCCAtuacao
	 * @throws ApplicationBusinessException
	 */
	private void verificarSituacaoCCAtuacao(DominioSituacao situacaoCCAtuacao)
			throws ApplicationBusinessException {
		if (situacaoCCAtuacao == null) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO);
		}

		if (situacaoCCAtuacao != DominioSituacao.A) {
			throw new ApplicationBusinessException(
					ServidorRNExceptionCode.MENSAGEM_CENTRO_CUSTO_ATUACAO_INATIVO);
		}

	}

	/**
	 * ORADB: Procedure RAPK_SER_RN.RN_SERP_VER_CC_LOT2
	 * 
	 * @param organogramaControleFrequencia
	 * @throws ApplicationBusinessException
	 */
	private void verificarOrganogramaCCLotacao(
			String organogramaControleFrequencia) throws ApplicationBusinessException {

		if (!isHCPA()) {
			return;
		}

		if (organogramaControleFrequencia == null) {
			throw new ApplicationBusinessException(
					ServidorRNExceptionCode.MENSAGEM_CENTRO_CUSTO_SEM_ORGANOGRAMA);
		}

	}

	/**
	 * Executar as operações realizadas pela enforce dos servidores
	 * 
	 * ORADB: Procedure RAPP_ENFORCE_SER_RULES
	 * 
	 * @param novo
	 * @param velho
	 * @throws ApplicationBusinessException
	 */
	private void ajustarRegrasPerfis(RapServidores novo, RapServidores velho)
			throws ApplicationBusinessException {

		this.alterouCCusto = "N";
		this.revogaFornecePerfis = false;
		this.ajustarNtEmail = false;
		
		Date dataAtual = DateUtil.obterDataComHoraInical(null);

		if (!CoreUtil.igual(novo.getCentroCustoLotacao(),
				velho.getCentroCustoLotacao())
				|| !CoreUtil.igual(novo.getCentroCustoAtuacao(),
						velho.getCentroCustoAtuacao())) {
			this.alterouCCusto = "S";
		}

		if (!CoreUtil.igual(novo.getCentroCustoLotacao(),
				velho.getCentroCustoLotacao())
				|| !CoreUtil.igual(novo.getCentroCustoAtuacao(),
						velho.getCentroCustoAtuacao())
				|| !CoreUtil.igual(novo.getOcupacaoCargo(),
						velho.getOcupacaoCargo())) {
			
			// Apenas seta o flag para qua a atualização dos perfis seja
			// realizada
			// após o merge da RapServidores. Problema enfrentado, pois a
			// procedure
			// necessita dos dados atualizados, para conceder/revogar perfis.
			this.revogaFornecePerfis = true;

		}

		if (velho.getDtFimVinculo() != null
				&& novo.getDtFimVinculo() == null
				&& (velho.getDtFimVinculo().before(dataAtual) || velho
						.getDtFimVinculo().equals(dataAtual))) {			
			this.ajustarNtEmail = true;
			/*
			getRegistroColaboradorRN().ajustarEmailNT(novo.getId().getMatricula(),
					novo.getId().getVinCodigo());
			*/
		}

		if (CoreUtil.igual(novo.getDtFimVinculo(), velho.getDtFimVinculo())
				&& novo.getDtFimVinculo() != null
				&& novo.getDtFimVinculo().after(dataAtual)) {
			this.ajustarNtEmail = true;
			/*
			getRegistroColaboradorRN().ajustarEmailNT(novo.getId().getMatricula(),
					novo.getId().getVinCodigo());
			*/
		}

	}	

	/**
	 * Verificar se houve exclusão da data de fim de vínculo
	 * 
	 * @param novo
	 * @param velho
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private boolean verificarExclusaoDataFimVinculo(RapServidores novo,
			RapServidores velho) throws ApplicationBusinessException {

		boolean excluir = false;

		if (velho.getDtFimVinculo() != null && novo.getDtFimVinculo() == null) {
			excluir = true;
		}

		return excluir;

	}
	
	/**
	 * Verificar se a data de fim de vínculo foi prorrogada para data futura
	 * 
	 * @param novo
	 * @param velho
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private boolean verificarProrrogacaoDataFimVinculo(RapServidores novo,
			RapServidores velho) throws ApplicationBusinessException {

		boolean prorrogou = false;
		Date dataAtual = DateUtil.obterDataComHoraInical(null);

		if (novo.getDtFimVinculo() != null
				&& novo.getDtFimVinculo().after(dataAtual)
				&& !CoreUtil.igual(novo.getDtFimVinculo(),
						velho.getDtFimVinculo())) {
			prorrogou = true;
		}

		return prorrogou;

	}

	/**
	 * Verificar se está sendo alterado um servidor com vínculo inativo
	 * 
	 * @param novo
	 * @param velho
	 * @return
	 */
	private boolean verificarAlteracaoServidorInativo(RapServidores novo,
			RapServidores velho) {

		boolean servidorInativo = false;
		Date dataAtual = DateUtil.obterDataComHoraInical(null);

		if (CoreUtil.igual(novo.getDtFimVinculo(), velho.getDtFimVinculo())
				&& novo.getDtFimVinculo() != null
				&& (novo.getDtFimVinculo().before(dataAtual) || novo
						.getDtFimVinculo().equals(dataAtual))) {
			servidorInativo = true;
		}

		return servidorInativo;

	}

	/**
	 * Verificar se a data de fim de vínculo foi alterada para uma data menor
	 * que a data atual
	 * 
	 * @param novo
	 * @param velho
	 * @return
	 */
	private boolean verificarAlteracaoDataFimVinculoMenor(RapServidores novo,
			RapServidores velho) {

		boolean alterouDataMenor = false;
		Date dataAtual = DateUtil.obterDataComHoraInical(null);

		if (velho.getDtFimVinculo() != null
				&& !CoreUtil.igual(novo.getDtFimVinculo(),
						velho.getDtFimVinculo())
				&& novo.getDtFimVinculo() != null
				&& novo.getDtFimVinculo().before(dataAtual)) {
			alterouDataMenor = true;
		}
		
		return alterouDataMenor;
	}	

	/**
	 * Verificar se a data de fim de vínculo foi alterada para mais de 365 dias
	 * 
	 * @param novo
	 * @param velho
	 * @return
	 */
	private boolean verificarDataFimVinculoSuperior365(RapServidores novo,
			RapServidores velho) {
		
		boolean alterouDataMenor = false;
		
		if(novo.getDtFimVinculo() == null){
			return alterouDataMenor;
		}
		
		Calendar dataAtual = Calendar.getInstance();
		dataAtual.set(Calendar.HOUR, 0);
		dataAtual.set(Calendar.MINUTE, 0);
		dataAtual.set(Calendar.SECOND, 0);
		dataAtual.add(Calendar.DAY_OF_MONTH, -365);

		if (velho.getDtFimVinculo() == null
				&& novo.getDtFimVinculo().before(dataAtual.getTime())) {
			alterouDataMenor = true;
		}

		return alterouDataMenor;
	}
	
	/**
	 * Montar as confirmações que serão apresentadas na tela conforme regra de
	 * negócio
	 * 
	 * @param novo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String montarConfirmacoes(RapServidores servidor, boolean edicao)
			throws ApplicationBusinessException {
		
		if (servidor == null) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO);
		}
		
		String mensagemRetorno = null;

		if(edicao){
			
			RapServidores servidorOld = getRapServidoresDAO().obterOld(servidor, true);
	
			if (this.verificarExclusaoDataFimVinculo(servidor, servidorOld)) {
				mensagemRetorno = "Atenção! Você está eliminando a data de "
						+ "fim de vínculo já existente para este servidor. Confirma ?";
			} else if (this.verificarProrrogacaoDataFimVinculo(servidor, servidorOld)) {
				mensagemRetorno = "Atenção! Você está prorrogando a data de "
						+ "fim de vínculo. Confirma ?";
			} else if (this.verificarAlteracaoServidorInativo(servidor, servidorOld)) {
				mensagemRetorno = "Atenção! Você está alterando os dados de um servidor "
						+ "que tem data de final de vínculo informada menor "
						+ "ou igual à data atual. Confirma ?";
			} else if (this.verificarAlteracaoDataFimVinculoMenor(servidor, servidorOld)) {
				mensagemRetorno = "Atenção! Você está tornando a data de fim de vínculo "
						+ "menor que a data atual. Confirma ?";
			} else if (this.verificarDataFimVinculoSuperior365(servidor, servidorOld)) {
				mensagemRetorno = "Atenção! Com esta data de fim de vínculo o servidor "
						+ "poderá perder o usuário na rede. Confirma ?";
			}
		}else{
			RapServidores outroContratoServidor = getRapServidoresDAO().buscarOutroContrato(servidor);
			if(outroContratoServidor != null){
				mensagemRetorno = "Atenção! Esta pessoa "
						+ servidor.getPessoaFisica().getCodigo()
						+ "-"
						+ servidor.getPessoaFisica().getNome()
						+ " em outro contrato já possui o usuário "
						+ outroContratoServidor.getUsuario()
						//+ " na rede que deve ser transferido para esta nova matrícula.";
						+ " associado. Verifique a necessidade de transferir para esta nova matrícula.";
			}
		}

		return mensagemRetorno;
	}	
	
	/**
	 * ORADB: Trigger RAPT_SER_BRI
	 * @param novo
	 * @param velho
	 * @throws ApplicationBusinessException
	 */	
	public void validarInclusaoServidores(RapServidores servidor)
			throws ApplicationBusinessException {
		if (servidor == null) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO);
		}
		
		if (servidor.getId().getMatricula() == null){
			throw new ApplicationBusinessException(
					ServidorRNExceptionCode.MENSAGEM_SERVIDOR_MATRICULA_NULA);
		}
		
		AghParametros paramPermiteMatriculaDuplicada = this
				.getParametroFacade().buscarAghParametro(
						AghuParametrosEnum.P_AGHU_PERMITE_MATRICULA_DUPLICADA);

		if (paramPermiteMatriculaDuplicada != null
				&& DominioSimNao.N.toString().equalsIgnoreCase(
						paramPermiteMatriculaDuplicada.getVlrTexto())) {
			getRapServidoresDAO().matriculaJaExiste(servidor.getId().getMatricula(), true);
		}

		boolean isHcpa = this.isHCPA();
		
		verificarCamposObrigatorios(servidor);
 
		if (isHcpa && (getObjetosOracleDAO().isProducaoHCPA()) ) {		
		
			if (servidor.getVinculo().getIndTransferencia() == DominioSimNao.S) {
				throw new ApplicationBusinessException(
						ServidorRNExceptionCode.MENSAGEM_INCLUSAO_VINCULO_CONTROLADO_STARH);	
			}
				
			if (!getObjetosOracleDAO().perfilVinculo(servidor.getVinculo().getCodigo(), servidor.getUsuario())) {
				throw new ApplicationBusinessException(
						ServidorRNExceptionCode.MENSAGEM_USUARIO_SEM_PERMISSAO_ALTERAR_SERVIDOR);
			}

			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			if (!getObjetosOracleDAO().permiteAlterarGPPG(servidor
							.getVinculo().getCodigo(), servidor.getOcupacaoCargo()
							.getId().getCodigo(), servidorLogado)) {
				throw new ApplicationBusinessException(
						ServidorRNExceptionCode.MENSAGEM_GPPG_SEM_PERMISSAO_ALTERAR);
			}
		
		}
		verificarDuplicidadeMatriculaVinculo(servidor);
		verificarExigenciaCPF(servidor);
		verificarVinculoServidor(servidor);
		verificarDataInicioVinculo(servidor.getDtInicioVinculo(),
				servidor.getDtFimVinculo());
		atualizarSituacaoServidor(servidor);
		verificarDataTerminoVinculo(servidor.getVinculo().getIndExigeTermino(),
				servidor.getDtFimVinculo());
		
		// Chamado somente na inserção, pois não é possível alterar o código da
		// pessoa e o vínculo
		getRapServidoresDAO().verificaDuplicidadePessoaVinculo(
				servidor.getPessoaFisica().getCodigo(), servidor.getVinculo()
						.getCodigo(), servidor.getId().getMatricula(), servidor
						.getVinculo().getIndPermDuplic());

		// Regra somente utilizada na atualização de servidores
		/*
		if(servidor.getCentroCustoLotacao() != null){
			verificarOrganogramaCCLotacao(servidor.getCentroCustoLotacao()
					.getOrganogramaCf());
		}
		*/

		if(servidor.getCentroCustoLotacao() != null){
			verificarSituacaoCCLotacao(servidor.getCentroCustoLotacao()
					.getIndSituacao());		
		}else{
			if(servidor.getCentroCustoAtuacao() != null){
				throw new ApplicationBusinessException(
						ServidorRNExceptionCode.MENSAGEM_CC_LOTACAO_OBRIGATORIO);				
			}
		}

		if(servidor.getCentroCustoAtuacao() != null){
			verificarSituacaoCCAtuacao(servidor.getCentroCustoAtuacao()
					.getIndSituacao());			
		}
		
		associarInfoAuditoria(servidor);

	}
	
	/**
	 * Verifica se o id do servidor está duplicado
	 * @param servidor
	 * @throws ApplicationBusinessException
	 */
	private void verificarDuplicidadeMatriculaVinculo(RapServidores servidor) throws ApplicationBusinessException {
		RapServidoresId id = new RapServidoresId();
		id.setMatricula(servidor.getId().getMatricula());
		id.setVinCodigo(servidor.getVinculo().getCodigo());
		RapServidores servidorBanco = getRapServidoresDAO().obter(id);
		if (servidorBanco != null){
			throw new ApplicationBusinessException(
					ServidorRNExceptionCode.SERVIDOR_ID_DUPLICADO);				
		}
		
	}
	
	/**
	 * ORADB: Trigger RAPT_SER_BRU
	 * @param novo
	 * @param velho
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void validarAtualizacaoServidores(RapServidores novo,
			RapServidores velho) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		
		if (novo == null || velho == null) {
			throw new IllegalArgumentException("Parâmetros obrigatórios.");
		}
		
		verificarCamposObrigatorios(novo);
		
		Date dataAtual = DateUtil.obterDataComHoraInical(null);
		
		if ((isHCPA())				
				&& (getObjetosOracleDAO().isProducaoHCPA())
				&& (!CoreUtil.igual(novo.getId().getMatricula(), velho.getId()
						.getMatricula())
						|| !CoreUtil.igual(novo.getId().getVinCodigo(), velho
								.getId().getVinCodigo())
						|| !CoreUtil.igual(novo.getCentroCustoLotacao(),
								velho.getCentroCustoLotacao())
						|| !CoreUtil.igual(novo.getOcupacaoCargo(),
								velho.getOcupacaoCargo())
						|| !CoreUtil.igual(novo.getPessoaFisica(),
								velho.getPessoaFisica())
						|| !CoreUtil.igual(novo.getDtInicioVinculo(),
								velho.getDtInicioVinculo())
						|| !CoreUtil.igual(novo.getDtFimVinculo(),
								velho.getDtFimVinculo())
						|| !CoreUtil.igual(novo.getGrupoFuncional(),
								velho.getGrupoFuncional())
						|| !CoreUtil.igual(novo.getTipoRemuneracao(),
								velho.getTipoRemuneracao())
						|| !CoreUtil.igual(novo.getCargaHoraria(),
								velho.getCargaHoraria())
						|| !CoreUtil.igual(novo.getHorarioPadrao(),
								velho.getHorarioPadrao()) || !CoreUtil.igual(
						novo.getCodStarh(), velho.getCodStarh()))) {

			if (novo.getVinculo().getIndTransferencia() == DominioSimNao.S) {
				throw new ApplicationBusinessException(
						ServidorRNExceptionCode.MENSAGEM_VINCULO_CONTROLADO_STARH);
			} 
			
			//verificar se será necessário buscar o código neste momento, pois o mesmo
			//já está sendo preenchido pela obterCodigoMatricula 
			/*
			else if (novo.getCodStarh() == null
					&& (novo.getDtFimVinculo() == null
							|| novo.getDtFimVinculo().after(dataAtual) || novo
							.getDtFimVinculo().equals(dataAtual))) {
				// aguardando retorno da Paula
				// falta implementar a chamada da RAPC_BUSCA_COD_STARH
				// não implementar o envio de email
			}*/
		}

		if (CoreUtil.igual(novo.getRamalTelefonico(),velho.getRamalTelefonico())
				&& CoreUtil.igual(novo.getUsuario(), velho.getUsuario())
				&& CoreUtil.igual(novo.getCentroCustoDesempenho(),velho.getCentroCustoDesempenho())
				&& !getObjetosOracleDAO().perfilVinculo(novo.getId().getVinCodigo(), servidorLogado != null ? servidorLogado.getUsuario() : null)) {			
			throw new ApplicationBusinessException(ServidorRNExceptionCode.MENSAGEM_USUARIO_SEM_PERMISSAO_ALTERAR_SERVIDOR);
		}
		
		/*
		 * Conforme definido na análise, não implementar regra de 
		 * validação para servidores pelo GPPG
		 * 
		if (!getRegistroColaboradorRN().permiteAlterarGPPG(novo.getVinculo()
				.getCodigo(), novo.getOcupacaoCargo().getId().getCodigo())) {
			throw new ApplicationBusinessException(
					ServidorRNExceptionCode.MENSAGEM_GPPG_SEM_PERMISSAO_ALTERAR);
		}
		*/

		if (!novo.getPessoaFisica().getNome().contains("USUARIO")
				&& (novo.getDtFimVinculo() == null || novo.getDtFimVinculo()
						.after(dataAtual))
				&& CoreUtil.igual(novo.getPessoaFisica(),
						velho.getPessoaFisica())) {

			this.verificarVinculoServidor(novo);

		}
			
		if ((velho.getDtFimVinculo() == null || (velho.getDtFimVinculo() != null && velho
				.getDtFimVinculo().after(dataAtual)))
				&& (novo.getDtFimVinculo() != null)
				&& (novo.getDtFimVinculo().before(dataAtual) || 
					novo.getDtFimVinculo().equals(dataAtual))
				&& novo.getCentroCustoLotacao() != null) {

			this.notificarEncerramentoContrato(novo);

		}
			
		if (!CoreUtil.igual(novo.getDtInicioVinculo(),
				velho.getDtInicioVinculo())
				|| !CoreUtil.igual(novo.getDtFimVinculo(),
						velho.getDtFimVinculo())) {
			this.verificarDataInicioVinculo(novo.getDtInicioVinculo(),
					novo.getDtFimVinculo());
		}
			
		if (!CoreUtil.igual(novo.getDtFimVinculo(), velho.getDtFimVinculo())) {
			atualizarSituacaoServidor(novo);
		}

		this.verificarDataTerminoVinculo(novo.getVinculo().getIndExigeTermino(),
				novo.getDtFimVinculo());

		if (novo.getCentroCustoLotacao() != null) {

			if (!CoreUtil.igual(novo.getCentroCustoLotacao(),
					velho.getCentroCustoLotacao())
					&& (novo.getIndSituacao() == DominioSituacaoVinculo.A || (novo
							.getIndSituacao() == DominioSituacaoVinculo.P && (novo
							.getDtFimVinculo().after(dataAtual) || novo
							.getDtFimVinculo().equals(dataAtual))))) {

				this.verificarSituacaoCCLotacao(novo.getCentroCustoLotacao()
						.getIndSituacao());

				// verificar o organograma do controle de frequencia
				if (novo.getId().getVinCodigo() == 1
						&& novo.getId().getMatricula() < 50000) {

					this.verificarOrganogramaCCLotacao(novo
							.getCentroCustoLotacao().getOrganogramaCf());
				}

			}

		}else{
			
			// Consiste Centro de Custo de Atuação informado e Centro de Custo
			// de Lotação não informado
			if (novo.getCentroCustoAtuacao() != null) {
				throw new ApplicationBusinessException(
						ServidorRNExceptionCode.MENSAGEM_CC_LOTACAO_OBRIGATORIO);
			}
			
		}
			
		if (novo.getCentroCustoAtuacao() != null
				&& !CoreUtil.igual(novo.getCentroCustoAtuacao(),
						velho.getCentroCustoAtuacao())) {
			this.verificarSituacaoCCAtuacao(novo.getCentroCustoAtuacao()
					.getIndSituacao());
		}

		if (novo.getDtFimVinculo() != null
				&& !CoreUtil.igual(novo.getDtFimVinculo(),
						velho.getDtFimVinculo())) {
			getRapServidoresDAO().verificarAfastamentoVigente(novo.getId().getVinCodigo(), novo
					.getId().getMatricula(), novo.getDtFimVinculo());
		}
		
		//atualizar as informações de vínculo/matrícula e data de alteração
		if (!CoreUtil.igual(novo.getId().getMatricula(), velho.getId()
				.getMatricula())
				|| !CoreUtil.igual(novo.getId().getVinCodigo(), velho
						.getId().getVinCodigo())
				|| !CoreUtil.igual(novo.getCentroCustoLotacao(),
						velho.getCentroCustoLotacao())
				|| !CoreUtil.igual(novo.getCentroCustoAtuacao(),
						velho.getCentroCustoAtuacao())
				|| !CoreUtil.igual(novo.getOcupacaoCargo(),
						velho.getOcupacaoCargo())
				|| !CoreUtil.igual(novo.getPessoaFisica(),
						velho.getPessoaFisica())
				|| !CoreUtil.igual(novo.getDtInicioVinculo(),
						velho.getDtInicioVinculo())
				|| !CoreUtil.igual(novo.getDtFimVinculo(),
						velho.getDtFimVinculo())
				|| !CoreUtil.igual(novo.getGrupoFuncional(),
						velho.getGrupoFuncional())
				|| !CoreUtil.igual(novo.getTipoRemuneracao(),
						velho.getTipoRemuneracao())
				|| !CoreUtil.igual(novo.getCargaHoraria(),
						velho.getCargaHoraria())
				|| !CoreUtil.igual(novo.getCodStarh(),
						velho.getCodStarh())
				|| !CoreUtil.igual(novo.getCentroCustoDesempenho(),
						velho.getCentroCustoDesempenho())) {
				
			associarInfoAuditoria(novo);

		}						
		
	}

	/**
	 * RAPF_MAN_SERVIDOR.PLL - RAP$PRE_INSERT_2
	 * @param servidor
	 * @throws ApplicationBusinessException
	 */
	private void verificarExigenciaCPF(RapServidores servidor)
			throws ApplicationBusinessException {

		if (servidor == null) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO);
		}

		Integer codigoNacionalidade = servidor.getPessoaFisica()
				.getAipNacionalidades().getCodigo();
		boolean exigeCpfRg = false;

		AghParametros paramCodigoNacionalidade = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_COD_NAC);

		if (!CoreUtil.igual(codigoNacionalidade, paramCodigoNacionalidade
				.getVlrNumerico().intValue())) {

			if (servidor.getVinculo().getIndExigeCpfRg() == DominioSimNao.S
					&& (servidor.getPessoaFisica().getCpf() == null || servidor
							.getPessoaFisica().getNroIdentidade() == null)) {

				exigeCpfRg = true;
			}

		} else {

			if (servidor.getPessoaFisica().getCpf() == null
					|| servidor.getPessoaFisica().getNroIdentidade() == null) {
				exigeCpfRg = true;
			}
		}

		if (exigeCpfRg) {
			throw new ApplicationBusinessException(
					ServidorRNExceptionCode.MENSAGEM_EXIGE_CPF_RG);
		}
	}
	
	/**
	 * Incluir as informações de servidor/matrícula e data de alteração
	 * @param servidor
	 * @throws ApplicationBusinessException 
	 */
	private void associarInfoAuditoria(RapServidores servidor) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		servidor.setServidor(servidorLogado);
		servidor.setAlteradoEm(new Date());
	}
	
	/**
	 * Enviar email notificando o encerramento de contrato
	 * @param novo
	 * @throws ApplicationBusinessException
	 */
	private void notificarEncerramentoContrato(RapServidores novo)
			throws ApplicationBusinessException {

		// Remetente
		AghParametros emailDe = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_ENVIO);
		if (emailDe.getVlrTexto() == null) {
			return;
		}

		// Destinatários
		List<String> listaDestinatarios = new ArrayList<String>();
		AghParametros emailDestino = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_CONSULTORES);
		if (emailDestino.getVlrTexto() == null) {
			return;
		}
		StringTokenizer emailPara = new StringTokenizer(
				emailDestino.getVlrTexto(), ";");
		while (emailPara.hasMoreTokens()) {
			listaDestinatarios.add(emailPara.nextToken().trim().toLowerCase());
		}

		// Assunto do Email
		String assuntoEmail = "RAPT_SER_BRU - Encerramento de contrato da matricula "
				+ novo.getId().getMatricula()
				+ " vinc "
				+ novo.getId().getVinCodigo()
				+ " nome "
				+ novo.getPessoaFisica().getNome()
				+ " consta como chefe do ccusto "
				+ novo.getCentroCustoLotacao().getCodigo()
				+ "-"
				+ novo.getCentroCustoLotacao().getDescricao();

		// Conteúdo do Email
		String conteudoEmail = "RAPT_SER_BRU - Encerramento de contrato da matricula "
				+ novo.getId().getMatricula()
				+ " vinc "
				+ novo.getId().getVinCodigo()
				+ " nome "
				+ novo.getPessoaFisica().getNome()
				+ " consta como chefe do ccusto "
				+ novo.getCentroCustoLotacao().getCodigo()
				+ "-"
				+ novo.getCentroCustoLotacao().getDescricao();

		// Realizar a chamada do envio de email
		getEmailUtil().enviaEmail(emailDe.getVlrTexto().toLowerCase(),
				listaDestinatarios, null, assuntoEmail, conteudoEmail);
	}

	/**
	 * ORABD: Trigger RAPT_SER_ARI
	 * @param servidor
	 * @param criacao
	 * @throws ApplicationBusinessException 
	 */	
	private void inserirHistCCustoDesempenho(RapServidores servidor) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Date dataAtual = new Date();
		
		RapHistCCustoDesempenhoId id = new RapHistCCustoDesempenhoId();
		id.setCctCodigo(servidor.getCentroCustoDesempenho().getCodigo());
		id.setSerMatricula(servidor.getId().getMatricula());
		id.setSerVinCodigo(servidor.getId().getVinCodigo());
		id.setDtInicio(dataAtual);
		
		RapHistCCustoDesempenho histCCustoDesempenho = new RapHistCCustoDesempenho(id);
		histCCustoDesempenho.setObservacao("Criado por RAPT_SER_ARI");
		histCCustoDesempenho.setServidorCriado(servidorLogado);
		histCCustoDesempenho.setCriadoEm(dataAtual);
		
		RapHistCCustoDesempenhoDAO rapHistCCustoDesempenhoDAO = this.getRapHistCCustoDesempenhoDAO();
		rapHistCCustoDesempenhoDAO.persistir(histCCustoDesempenho);
		rapHistCCustoDesempenhoDAO.flush();
	}

	/**
	 * ORABD: Atualização da Rap_Hist_CCusto_Desempenho através da trigger RAPT_SER_ARU
	 * @param servidor
	 * @param criacao
	 * @throws ApplicationBusinessException 
	 */	
	private void inserirHistCCustoDesempenho(Short codigoVinculo, Integer matricula, Integer codigoCctDesempenho) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
				
		Date dataAtual = new Date();
		
		RapHistCCustoDesempenhoId id = new RapHistCCustoDesempenhoId();
		id.setCctCodigo(codigoCctDesempenho);
		id.setSerMatricula(matricula);
		id.setSerVinCodigo(codigoVinculo);
		id.setDtInicio(dataAtual);
		
		RapHistCCustoDesempenho histCCustoDesempenho = new RapHistCCustoDesempenho(id);		
		histCCustoDesempenho.setServidorCriado(servidorLogado);
		histCCustoDesempenho.setCriadoEm(dataAtual);
		
		RapHistCCustoDesempenhoDAO rapHistCCustoDesempenhoDAO = this.getRapHistCCustoDesempenhoDAO();
		rapHistCCustoDesempenhoDAO.persistir(histCCustoDesempenho);
		rapHistCCustoDesempenhoDAO.flush();
	}
	
	/**
	 * Atualização do Centro de Custo de Desempenho - RAP_HIST_CCUSTO_DESEMPENHO
	 * @param codigoVinculo
	 * @param matricula
	 * @param codigoCctDesempenhoNovo
	 * @param codigoCctDesempenhoVelho
	 * @throws ApplicationBusinessException 
	 */
	private void atualizarHistCCustoDesempenho(Short codigoVinculo,
			Integer matricula, FccCentroCustos centroCustoDesempenhoNovo,
			FccCentroCustos centroCustoDesempenhoVelho) throws ApplicationBusinessException {

		if(centroCustoDesempenhoVelho == null
				&& centroCustoDesempenhoNovo == null){
			return;
		}
				
		Integer codigoCctDesempenhoNovo = null;
		Integer codigoCctDesempenhoVelho = null;
		
		if(centroCustoDesempenhoNovo!=null){
			codigoCctDesempenhoNovo = centroCustoDesempenhoNovo.getCodigo();	
		}
				
		if(centroCustoDesempenhoVelho!=null){
			codigoCctDesempenhoVelho = centroCustoDesempenhoVelho.getCodigo();	
		}
				
		if (codigoCctDesempenhoVelho != null
				&& centroCustoDesempenhoNovo != null
				&& !CoreUtil.igual(codigoCctDesempenhoNovo,
						codigoCctDesempenhoVelho)) {

			RapHistCCustoDesempenho histCCustoDesempenho = getRapHistCCustoDesempenhoDAO()
					.obterHistCCustoDesempenho(codigoVinculo, matricula,
							codigoCctDesempenhoVelho);
			if (histCCustoDesempenho != null) {
				Calendar dataAtual = Calendar.getInstance();
				dataAtual.add(Calendar.DAY_OF_MONTH, -1);
				histCCustoDesempenho.setDtFim(dataAtual.getTime());
				getRapHistCCustoDesempenhoDAO().merge(histCCustoDesempenho);				
			}

			this.inserirHistCCustoDesempenho(codigoVinculo, matricula,
					codigoCctDesempenhoNovo);

		}

		if (codigoCctDesempenhoVelho == null && codigoCctDesempenhoNovo != null) {
			this.inserirHistCCustoDesempenho(codigoVinculo, matricula,
					codigoCctDesempenhoNovo);
		}

		if (codigoCctDesempenhoVelho != null && codigoCctDesempenhoNovo == null) {

			RapHistCCustoDesempenho histCCustoDesempenho = getRapHistCCustoDesempenhoDAO()
					.obterHistCCustoDesempenho(codigoVinculo, matricula,
							codigoCctDesempenhoVelho);

			if (histCCustoDesempenho != null) {
				Calendar dataAtual = Calendar.getInstance();
				dataAtual.add(Calendar.DAY_OF_MONTH, -1);
				histCCustoDesempenho.setDtFim(dataAtual.getTime());
				getRapHistCCustoDesempenhoDAO().merge(histCCustoDesempenho);				
			}

		}
		
	}
	
	/**
	 * Utilizado para efetuar a gravação de servidor no banco de dados - tabela
	 * Rap_Servidores Além disso insere registros na tabela
	 * Rap_Hist_CCusto_Desempenho
	 * 
	 * @param servidor
	 */
	// @Transactional(TransactionPropagationType.REQUIRED)
	public void inserirServidor(RapServidores servidor)
			throws ApplicationBusinessException {
		RapCodStarhLivresDAO rapCodStarhLivresDAO = this.getCodStarhLivresDAO();
		
		this.obterCodigoMatricula(servidor);
		if (servidor.getCentroCustoDesempenho() != null) {
			this.inserirHistCCustoDesempenho(servidor);
		}

		RapServidoresDAO rapServidoresDAO = getRapServidoresDAO();
		//Caso tenha usado um rapCodStarhLivres para a matrícula deste servidor, remove da tabela
		RapCodStarhLivres rapCodStarhLivres = rapCodStarhLivresDAO.obterPorChavePrimaria(servidor.getId().getMatricula());
		if (rapCodStarhLivres != null){
			rapCodStarhLivresDAO.remover(rapCodStarhLivres);
			rapCodStarhLivresDAO.flush();
		}
		
		rapServidoresDAO.persistir(servidor);
		rapServidoresDAO.flush();
		
		// executar os procedimentos contidos na evt_post_insert da PLL
		// somente para HCPA e no ambiente de produção para evitar a criação
		// indevida de usuários na rede

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (isHCPA()
				&& getObjetosOracleDAO().isProducaoHCPA()) {		
			getObjetosOracleDAO().fornecerPerfisServidor(servidor.getId()
					.getMatricula(), servidor.getId().getVinCodigo(), null,
					null, servidorLogado);
		}		

	}
	
	protected RapCodStarhLivresDAO getCodStarhLivresDAO() {
		return rapCodStarhLivresDAO;
	}
	
	/**
	 * Atualiza as informações de servidores
	 * @param servidor
	 * @param servidorOriginal
	 * @throws ApplicationBusinessException
	 */
	// @Transactional(TransactionPropagationType.REQUIRED)
	public void atualizarServidor(RapServidores servidor,
			RapServidores servidorOriginal) throws ApplicationBusinessException {
		
		this.ativarNtEmail = false;
		this.atualizarJournal(servidorOriginal);
		
		this.atualizarHistCCustoDesempenho(servidorOriginal.getId()
				.getVinCodigo(), servidorOriginal.getId().getMatricula(),
				servidor.getCentroCustoDesempenho(), servidorOriginal
						.getCentroCustoDesempenho());
								
		if (isHCPA()){
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			//Envio de email realizado pela RAPT_SER_ARU 
			this.notificarTrocaServidorHorista(servidor, servidorOriginal);
			getObjetosOracleDAO().avisarEncerramentoVinculo(servidor.getId()
					.getMatricula(), servidor.getId().getVinCodigo(), servidorLogado);

			this.atualizarLideresPorCentroCusto(servidor, servidorOriginal);
			
			this.ajustarRegrasPerfis(servidor, servidorOriginal);
						
			if (getObjetosOracleDAO().isProducaoHCPA()) {
				// Procedimento realizado pela rapp_verifica_conta_nt_email
				// localizado na RAPF_MAN_SERVIDOR.PLL
				if (verificarExclusaoDataFimVinculo(servidor, servidorOriginal)
						|| verificarProrrogacaoDataFimVinculo(servidor,
								servidorOriginal)) {
					this.ativarNtEmail = true;
					/*
					getRegistroColaboradorRN().ativarContaNtEmail(servidor.getId()
							.getMatricula(), servidor.getId().getVinCodigo());
					*/
				}
			}
						
		}
		RapServidoresDAO rapServidoresDAO = getRapServidoresDAO();
		rapServidoresDAO.merge(servidor);		
		rapServidoresDAO.flush();
		
		// Realizar a atualização dos perfis e ativação/ajuste da conta de
		// email.
		if (isHCPA()) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			if (this.revogaFornecePerfis) {
				getObjetosOracleDAO().revogarPerfisServidor(servidor.getId()
						.getMatricula(), servidor.getId().getVinCodigo(),
						this.alterouCCusto, null, servidorLogado);
				getObjetosOracleDAO().fornecerPerfisServidor(servidor.getId()
						.getMatricula(), servidor.getId().getVinCodigo(),
						this.alterouCCusto, null, servidorLogado);
			}

			if (this.ajustarNtEmail) {
				getObjetosOracleDAO().ajustarEmailNT(servidor.getId()
						.getMatricula(), servidor.getId().getVinCodigo(), servidorLogado);
			}

			if (this.ativarNtEmail) {
				getObjetosOracleDAO().ativarContaNtEmail(servidor.getId()
						.getMatricula(), servidor.getId().getVinCodigo(), servidorLogado);
			}

		}
		
	}	
	
	/**
	 * Atualização da journal RAP_SERVIDORES_JN
	 * @param servidorOld
	 */
	// @Transactional(TransactionPropagationType.REQUIRED)
	@SuppressWarnings("PMD.NPathComplexity")
	public void atualizarJournal(RapServidores servidorOld){
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		RapServidoresJn servidorJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, RapServidoresJn.class, servidorLogado.getUsuario());
		
		if(servidorOld.getId() != null){
			servidorJn.setMatricula(servidorOld.getId().getMatricula());
			servidorJn.setVinCodigo(servidorOld.getId().getVinCodigo());			
		}
		if(servidorOld.getCentroCustoLotacao() != null){
			servidorJn.setCctCodigo(servidorOld.getCentroCustoLotacao().getCodigo());	
		}
		
		if(servidorOld.getOcupacaoCargo() != null && servidorOld.getOcupacaoCargo().getId() != null ){
			servidorJn.setOcaCarCodigo(servidorOld.getOcupacaoCargo().getId().getCargoCodigo());
			servidorJn.setOcaCodigo(servidorOld.getOcupacaoCargo().getId().getCodigo());			
		}
		
		if(servidorOld.getPessoaFisica() != null){
			servidorJn.setPesCodigo(servidorOld.getPessoaFisica().getCodigo());	
		}
		
		servidorJn.setDtInicioVinculo(servidorOld.getDtInicioVinculo());
		servidorJn.setDtFimVinculo(servidorOld.getDtFimVinculo());
		
		if(servidorOld.getHorarioTrabalho() != null){
			servidorJn.setHtrCodigo(servidorOld.getHorarioTrabalho().getCodigo());	
		}
		
		if(servidorOld.getCentroCustoAtuacao() != null){
			servidorJn.setCctCodigoAtua(servidorOld.getCentroCustoAtuacao().getCodigo());	
		}
		
		servidorJn.setAlteradoEm(servidorOld.getAlteradoEm());
		
		if(servidorOld.getServidor() != null && servidorOld.getServidor().getId() != null){
			servidorJn.setSerMatricula(servidorOld.getServidor().getId().getMatricula());
			servidorJn.setSerVinCodigo(servidorOld.getServidor().getId().getVinCodigo());			
		}
		servidorJn.setUsuario(servidorOld.getUsuario());
		servidorJn.setEmail(servidorOld.getEmail());
		
		if(servidorOld.getGrupoFuncional() != null){
			servidorJn.setGrfCodigo(servidorOld.getGrupoFuncional().getCodigo());	
		}
				
		if(servidorOld.getIndSituacao() != null){
			servidorJn.setIndSituacao(servidorOld.getIndSituacao().toString());	
		}
		
		if(servidorOld.getRamalTelefonico() != null && servidorOld.getRamalTelefonico().getNumeroRamal() != null){
			servidorJn.setRamNroRamal(servidorOld.getRamalTelefonico().getNumeroRamal().shortValue());	
		}
		
		servidorJn.setTipoRemuneracao(servidorOld.getTipoRemuneracao());
		servidorJn.setCargaHoraria(servidorOld.getCargaHoraria());
		
		if(servidorOld.getCentroCustoDesempenho() != null){
			servidorJn.setCctCodigoDesempenho(servidorOld.getCentroCustoDesempenho().getCodigo());	
		}
		
		RapServidoresJnDAO rapServidoresJnDAO = this.getRapServidoresJnDAO();
		rapServidoresJnDAO.persistir(servidorJn);
		rapServidoresJnDAO.flush();
	}
	
	/**
	 * Enviar email quando houver alteração de centro de custo de lotação para
	 * servidor horista
	 * 
	 * @param novo
	 * @param velho
	 * @throws ApplicationBusinessException
	 */
	private void notificarTrocaServidorHorista(RapServidores novo,
			RapServidores velho) throws ApplicationBusinessException {

		if (novo.getTipoRemuneracao() == DominioTipoRemuneracao.H
				&& !CoreUtil.igual(novo.getCentroCustoLotacao(),
						velho.getCentroCustoLotacao())) {

			// Remetente
			AghParametros emailDe = getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_ENVIO);

			if (emailDe.getVlrTexto() == null) {
				return;
			}

			// Destinatários
			List<String> listaDestinatarios = new ArrayList<String>();
			AghParametros emailDestino = getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_USUARIO_PONTO);
			if (emailDestino.getVlrTexto() == null) {
				return;
			}

			StringTokenizer emailPara = new StringTokenizer(
					emailDestino.getVlrTexto(), ";");
			while (emailPara.hasMoreTokens()) {
				listaDestinatarios.add(emailPara.nextToken().trim()
						.toLowerCase());
			}

			// Assunto do Email
			String assuntoEmail = "Troca de ccusto de servidor horista";

			Integer codigoCctLotacaoNovo = null;
			Integer codigoCctLotacaoVelho = null;

			if (novo.getCentroCustoLotacao() != null) {
				codigoCctLotacaoNovo = novo.getCentroCustoLotacao().getCodigo();
			}

			if (velho.getCentroCustoLotacao() != null) {
				codigoCctLotacaoVelho = velho.getCentroCustoLotacao()
						.getCodigo();
			}

			// Conteúdo do Email
			String conteudoEmail = "Troca de ccusto de servidor horista com contrato "
					+ novo.getId().getMatricula()
					+ " do "
					+ codigoCctLotacaoVelho + " para " + codigoCctLotacaoNovo;

			// Realizar a chamada do envio de email
			getEmailUtil().enviaEmail(emailDe.getVlrTexto().toLowerCase(),
					listaDestinatarios, null, assuntoEmail, conteudoEmail);
		}
	}
	
	/**
	 * Realizar a atualização dos líderes conforme alteração realizada no centro de custo de desempenho/lotação
	 * @param novo
	 * @param velho
	 * @throws ApplicationBusinessException
	 */
	private void atualizarLideresPorCentroCusto(RapServidores novo,
			RapServidores velho) throws ApplicationBusinessException {

		if ((!CoreUtil.igual(novo.getCentroCustoDesempenho(),
				velho.getCentroCustoDesempenho()) || !CoreUtil
				.igual(novo.getCentroCustoLotacao(),
						velho.getCentroCustoLotacao()))) {

			Integer centroCustoDesempenho = null;
			Integer centroCustoDesempenhoVelho = null;
			Integer centroCustoLotacao = null;
			Integer centroCustoLotacaoVelho = null;

			if (novo.getCentroCustoDesempenho() != null) {
				centroCustoDesempenho = novo.getCentroCustoDesempenho()
						.getCodigo();
			}

			if (velho.getCentroCustoDesempenho() != null) {
				centroCustoDesempenhoVelho = velho
						.getCentroCustoDesempenho().getCodigo();
			}

			if (novo.getCentroCustoLotacao() != null) {
				centroCustoLotacao = novo.getCentroCustoLotacao()
						.getCodigo();
			}

			if (velho.getCentroCustoLotacao() != null) {
				centroCustoLotacaoVelho = velho
						.getCentroCustoLotacao().getCodigo();
			}

			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			getObjetosOracleDAO().atualizarLideres(centroCustoDesempenho,
					centroCustoDesempenhoVelho, centroCustoLotacao,
					centroCustoLotacaoVelho, novo.getId().getVinCodigo(),
					velho.getId().getVinCodigo(), novo.getId()
							.getMatricula(), velho.getId()
							.getMatricula(), novo.getPessoaFisica()
							.getNome(), servidorLogado);
		}

	}
	
	/**
	 * Obter o código de matrícula
	 * @param servidor
	 * @throws ApplicationBusinessException
	 */
	private void obterCodigoMatricula(RapServidores servidor)
			throws ApplicationBusinessException {

		Integer codigoMatricula = null;

		if (isHCPA()) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			codigoMatricula = getObjetosOracleDAO().buscarCodigoMatricula(servidorLogado);
			if (codigoMatricula == null) {
				throw new ApplicationBusinessException(
						ServidorRNExceptionCode.MENSAGEM_PROBLEMA_GERACAO_MATRICULA);
			} else {
				servidor.getId().setMatricula(codigoMatricula);				
			}
			
			servidor.setCodStarh(servidor.getId().getMatricula());
		} 		
		servidor.getId().setVinCodigo(servidor.getVinculo().getCodigo());
	}
	
	protected RapServidoresJnDAO getRapServidoresJnDAO(){
		return rapServidoresJnDAO;
	}
	
	/**
	 * ORABD: RAPC_IDADE_EXT
	 * @param dataNascimento
	 * @return
	 * @throws ApplicationBusinessException
	 */	
	public String obterIdadeExtenso(Date dataParametro)
			throws ApplicationBusinessException {

		if (dataParametro == null) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO);

		}

		DateTime dataAtual = new DateTime();
		DateTime dataNascimento = new DateTime(dataParametro);

		if (dataNascimento.isAfter(dataAtual)) {
			throw new ApplicationBusinessException(
					ServidorRNExceptionCode.MENSAGEM_DATA_NASCIMENTO_SUPERIOR_DATA_ATUAL);
		}

		Integer meses = Months.monthsBetween(dataNascimento, dataAtual)
				.getMonths() % 12;
		Integer anos = Years.yearsBetween(dataNascimento, dataAtual).getYears();

		String idadeExtenso;
		if(meses==1){
			idadeExtenso = anos + " anos " + meses + " mes";
		} else {
			idadeExtenso = anos + " anos " + meses + " meses";
		}
		return idadeExtenso;

	}

	
	/**
	 * ORABD: RAPC_TEMPO_EXT
	 * 
	 * @param dataNascimento
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String obterTempoExtenso(Date dataInicioVinculo, Date dataFimVinculo)
			throws ApplicationBusinessException {

		if (dataInicioVinculo == null) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO);
		}

		if (dataFimVinculo == null) {
			dataFimVinculo = DateUtil.obterDataComHoraInical(null);
		}

		Integer meses = 0;
		Integer anos = 0;
		
		if (!dataInicioVinculo.after(dataFimVinculo)) {
			DateTime dataInicio = new DateTime(dataInicioVinculo);
			DateTime dataFim = new DateTime(dataFimVinculo);

			meses = Months.monthsBetween(dataInicio, dataFim).getMonths() % 12;
			anos = Years.yearsBetween(dataInicio, dataFim).getYears();
		}
		
		String mesExtenso;
		String anoExtenso;

		if (meses == 1) {
			mesExtenso = meses + " mes";
		} else {
			mesExtenso = meses + " meses";
		}

		if (anos == 1) {
			anoExtenso = anos + " ano";
		} else {
			anoExtenso = anos + " anos";
		}

		return anoExtenso + " " + mesExtenso;

	}
	
	/**
	 * Retornar infomação de afastamento
	 * 
	 * @param dataFimVinculo
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	public String obterAfastamento(Date dataFimVinculo, Integer matricula,
			Short vinCodigo) {
	
		Date dataAtual = DateUtil.obterDataComHoraInical(null);
		String retorno = null;
		if (dataFimVinculo == null || dataFimVinculo.after(dataAtual)
				|| dataFimVinculo.equals(dataAtual)) {

			if (getRapAfastamentoDAO().verificarAfastamento(matricula, vinCodigo)) {
				retorno = Descricao.AFASTADO.toString();
			}
		}else{
			retorno = Descricao.INATIVO.toString();
		}
		return retorno;
	}
	
	/**
	 * Verificar se a pessoa física e o vínculo foram informados
	 * 
	 * @param servidor
	 * @throws ApplicationBusinessException
	 */
	private void verificarCamposObrigatorios(RapServidores servidor)
			throws ApplicationBusinessException {

		if (servidor == null) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO);
		}

		if (servidor.getPessoaFisica() == null) {
			throw new ApplicationBusinessException(
					ServidorRNExceptionCode.MENSAGEM_PESSOA_FISICA_OBRIGATORIO);
		}

		if (servidor.getVinculo() == null) {
			throw new ApplicationBusinessException(
					ServidorRNExceptionCode.MENSAGEM_VINCULO_OBRIGATORIO);
		}

	}

	protected ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}
	
	protected RapServidoresDAO getRapServidoresDAO() {
		return rapServidoresDAO;
	}
	
	protected RapHistCCustoDesempenhoDAO getRapHistCCustoDesempenhoDAO() {
		return rapHistCCustoDesempenhoDAO;
	}
	
	protected RapAfastamentoDAO getRapAfastamentoDAO() {
		return rapAfastamentoDAO;
	}
	
	protected EmailUtil getEmailUtil() {
		return this.emailUtil;
	}		

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
