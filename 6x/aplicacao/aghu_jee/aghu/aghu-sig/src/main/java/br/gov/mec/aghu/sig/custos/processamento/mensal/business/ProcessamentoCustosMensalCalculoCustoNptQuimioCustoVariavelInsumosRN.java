package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoConta;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoValorConta;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.SigCalculoAtdConsumo;
import br.gov.mec.aghu.model.SigCalculoDetalheConsumo;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoExceptionCode;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.SigCalculoAtendimentoPacienteVO;
import br.gov.mec.aghu.sig.dao.SigCalculoAtdPacienteDAO;
import br.gov.mec.aghu.sig.dao.SigMvtoContaMensalDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Estória do Usuário #32261 - Efetuar cálculo de custo de npt, bolsas, seringas e dispensações de quimioterapias - custo variável insumos
 * @author rogeriovieira
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalCalculoCustoNptQuimioCustoVariavelInsumosRN.class)
public class ProcessamentoCustosMensalCalculoCustoNptQuimioCustoVariavelInsumosRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = -2815643332143968100L;

	@Override
	public String getTitulo() {
		return "Efetuar cálculo de custo de npt, bolsas, seringas e dispensações de quimioterapias - custo variável insumos";
	}

	@Override
	public String getNome() {
		return "CalculoCustoNptQuimioCustoVariavelInsumosRN";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 33; //Solicitar pro Renato o nome da tabela pra consulta do calculo
	}
	
	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos, DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {

		// Passo 01
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores,sigProcessamentoPassos, DominioEtapaProcessamento.V, "INICIO_PROCESSAMENTO_32216");

		// Consulta paramentro
		
		Integer codigoCC;
		try {
			AghParametros parametro = this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_SIG_CODIGO_CC_MISTURAS_INTRAVENOSAS);
			codigoCC = parametro.getVlrNumerico().intValue();
		} catch (ApplicationBusinessException e) {
			
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.V, "PARAMETRO_NAO_CADASTRADO", AghuParametrosEnum.P_AGHU_SIG_CODIGO_CC_MISTURAS_INTRAVENOSAS);
			throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_BUSCA_PARAMETROS, "P_AGHU_SIG_CODIGO_CC_MISTURAS_INTRAVENOSAS");
		}
		// Passo 02
		List<SigCalculoAtendimentoPacienteVO> consultaC1 = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO()
				.buscarSigCalculoAtendimentoPacienteVOPorProcessamentoECodigoCentroCusto(sigProcessamentoCusto.getSeq(), codigoCC, SigCalculoAtdPacienteDAO.C1);

		this.consulta01(sigProcessamentoCusto, consultaC1);

		// Passo 04
		List<SigCalculoAtendimentoPacienteVO> consultaC3 = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO()
				.buscarSigCalculoAtendimentoPacienteVOPorProcessamentoECodigoCentroCusto(sigProcessamentoCusto.getSeq(), codigoCC, SigCalculoAtdPacienteDAO.C3);
		this.consulta03(consultaC3);

		// Passo 06
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,sigProcessamentoPassos, DominioEtapaProcessamento.V, "VALOR_PREVISTO_INSUMO_CALCULADO_SUCESSO");

		// Passo 07 (Repete os passos 08, 09, 10 e 11 para cada insumo recuperado no passo 07)
		this.consulta04(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, codigoCC);
		
		// Passo 13 (Repete os passos 14, 15,16 e 17 para cada insumo recuperadono passo 13.)
		List<SigCalculoAtendimentoPacienteVO> consultaC7 = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO()
				.buscarSigCalculoAtendimentoPacienteVOPorProcessamentoECodigoCentroCusto(sigProcessamentoCusto.getSeq(),codigoCC, SigCalculoAtdPacienteDAO.C7);
		this.consulta07(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, codigoCC, consultaC7);

		// Passo 19
		List<SigCalculoAtendimentoPacienteVO> consultaC9 = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO()
				.buscarSigCalculoAtendimentoPacienteVOPorProcessamentoECodigoCentroCusto(sigProcessamentoCusto.getSeq(), codigoCC, SigCalculoAtdPacienteDAO.C9);
		this.consulta09(sigProcessamentoCusto, codigoCC, consultaC9);

		// Passo 21
		List<SigCalculoAtendimentoPacienteVO> consultaC10 = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO()
				.buscarSigCalculoAtendimentoPacienteVOPorProcessamentoECodigoCentroCusto(sigProcessamentoCusto.getSeq(), codigoCC, SigCalculoAtdPacienteDAO.C10);
		this.consulta10(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, consultaC10);

		// Passo 24
		List<SigCalculoAtendimentoPacienteVO> consultaC11 = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO()
				.buscarValoresSigCalculoDetalhePaciente(sigProcessamentoCusto.getSeq(), codigoCC);
		this.consulta11(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, codigoCC, consultaC11);
	}

	private void consulta03(List<SigCalculoAtendimentoPacienteVO> consultaC3) throws ApplicationBusinessException {
		for (SigCalculoAtendimentoPacienteVO voC3 : consultaC3) {
			
			SigCalculoAtdConsumo consumo = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().obterPorChavePrimaria(voC3.getSigCalculoAtdConsumoSeq());
			consumo.setValorParcialPrevistoInsumos(consumo.getValorParcialPrevistoInsumos().add(voC3.getValorPrevisto()));

			// Passo 05
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().atualizar(consumo);
			this.commitProcessamentoCusto();
		}
	}

	private void consulta11(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, Integer codigoCC, List<SigCalculoAtendimentoPacienteVO> consultaC11) throws ApplicationBusinessException {
		for (SigCalculoAtendimentoPacienteVO voC11 : consultaC11) {
			
			FccCentroCustos centroCusto = this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(codigoCC);
			ScoMaterial material = this.getProcessamentoCustoUtils().getComprasFacade().obterScoMaterialPorChavePrimaria(voC11.getSigCalculoDetalheConsumoScoMaterialCodigo());
			Object[] valorMedioInsumo = this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO()
					.buscarCustoMedioMaterialComUnidadeMedida(sigProcessamentoCusto.getSeq(), centroCusto.getCodigo(), material.getCodigo());
			// Passo 25
			SigMvtoContaMensal sigMvtoContaMensal = this.criarSigMvtoContaMensal(sigProcessamentoCusto, rapServidores, centroCusto,
					material, voC11.getQuantidadeConsumido().longValue(), (BigDecimal) valorMedioInsumo[0], voC11.getValorConsumido().multiply(new BigDecimal(-1)));
			this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().persistir(sigMvtoContaMensal);
			this.commitProcessamentoCusto();
		}

		// Passo 26
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,sigProcessamentoPassos, DominioEtapaProcessamento.V, "VALOR_INSUMOS_DEBITADO");
	}

	private void consulta10(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, List<SigCalculoAtendimentoPacienteVO> consultaC10) throws ApplicationBusinessException {
		for (SigCalculoAtendimentoPacienteVO voC10 : consultaC10) {
			
			SigCalculoAtdConsumo consumo = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().obterPorChavePrimaria(voC10.getSigCalculoAtdConsumoSeq());
			consumo.setValorParcialConsumidoInsumos(consumo.getValorParcialConsumidoInsumos().add(voC10.getValorConsumido()));

			// Passo 22
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().atualizar(consumo);
			this.commitProcessamentoCusto();

			// Passo 23
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.V, "VALOR_CONSUMIDO_INSUMOS_SUCESSO");
		}
	}

	private void consulta09(SigProcessamentoCusto sigProcessamentoCusto, Integer codigoCC, List<SigCalculoAtendimentoPacienteVO> consultaC9) throws ApplicationBusinessException {
		for (SigCalculoAtendimentoPacienteVO voC9 : consultaC9) {

			List<SigCalculoAtendimentoPacienteVO> consultaC96 = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO()
					.buscarValoresSigCalculoDetalhePaciente(sigProcessamentoCusto.getSeq(), codigoCC, voC9.getSigCalculoDetalheConsumoScoMaterialCodigo());

			if (consultaC96 != null) {
				for (SigCalculoAtendimentoPacienteVO voC96 : consultaC96) {
					SigCalculoDetalheConsumo detalheConsumo = this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().obterPorChavePrimaria(voC96.getSigCalculoDetalheConsumoSeq());
					detalheConsumo.setQtdeConsumido(voC9.getQuantidadePrevisto());
					detalheConsumo.setValorConsumido(voC9.getValorPrevisto());

					this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().atualizar(detalheConsumo);
					this.commitProcessamentoCusto();
				}
			}
		}
	}

	private void consulta07(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, Integer codigoCC, List<SigCalculoAtendimentoPacienteVO> consultaC7) {
		for (SigCalculoAtendimentoPacienteVO voC7 : consultaC7) {

			// Passo 14
			Integer quandidadeDisponivel = this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO()
					.buscarConsumoExcedenteInsumos(voC7.getSigCalculoDetalheConsumoScoMaterialCodigo(), codigoCC, sigProcessamentoCusto, voC7.getQuantidadePrevisto().longValue(), SigMvtoContaMensalDAO.CONDICAO_MAIOR);

			if (quandidadeDisponivel != null && !quandidadeDisponivel.equals(0)) {
				
				// Passo 15
				List<SigCalculoAtendimentoPacienteVO> consultaC76 = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO()
						.buscarValoresSigCalculoDetalhePaciente(sigProcessamentoCusto.getSeq(), codigoCC, voC7.getSigCalculoDetalheConsumoScoMaterialCodigo());
				
				if (consultaC76 != null) {
					for (SigCalculoAtendimentoPacienteVO voC76 : consultaC76) {
						SigCalculoDetalheConsumo detalheConsumo = this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().obterPorChavePrimaria(voC76.getSigCalculoDetalheConsumoSeq());
						// Passo 16
						// QTDE_CONSUMIDO = (QTDE_PREVISTO / QTDE_PREVISTO_TOTAL) * QTDE_DISPONIVEL_TOTAL
						detalheConsumo.setQtdeConsumido(ProcessamentoCustoUtils.dividir(voC76.getQuantidadePrevisto(),voC7.getQuantidadePrevistoTotal()).multiply(new BigDecimal(quandidadeDisponivel)));

						// VLR_CONSUMIDO = (VLR_PREVISTO / QTDE_PREVISTO) * QTDE_CONSUMIDO
						detalheConsumo.setValorConsumido(ProcessamentoCustoUtils.dividir(voC76.getValorPrevisto(),voC76.getQuantidadePrevisto()).multiply(detalheConsumo.getQtdeConsumido()));

						this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().atualizar(detalheConsumo);
					}
				}
			}
			// Passo 17
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.V, "INSUMO_VALORES_AJUSTADOS_CONSUMO_PREVISTO_BAIXO", voC7.getSigCalculoDetalheConsumoScoMaterialCodigo());
		}
	}

	private void consulta04(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, Integer codigoCC) throws ApplicationBusinessException {
		
		List<SigCalculoAtendimentoPacienteVO> consultaC4 = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO()
				.buscarSigCalculoAtendimentoPacienteVOPorProcessamentoECodigoCentroCusto(sigProcessamentoCusto.getSeq(),codigoCC, SigCalculoAtdPacienteDAO.C4);
		
		for (SigCalculoAtendimentoPacienteVO voC4 : consultaC4) {
			// Passo 08
			Integer quantidadePrevistaTotal = this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO()
					.buscarConsumoExcedenteInsumos(voC4.getSigCalculoDetalheConsumoScoMaterialCodigo(), codigoCC, sigProcessamentoCusto, voC4.getQuantidadePrevisto().longValue(), SigMvtoContaMensalDAO.CONDICAO_MENOR);

			if (quantidadePrevistaTotal != null && !quantidadePrevistaTotal.equals(0)) {
				// Passo 09
				List<SigCalculoAtendimentoPacienteVO> consultaC6 = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO()
						.buscarValoresSigCalculoDetalhePaciente(sigProcessamentoCusto.getSeq(), codigoCC, voC4.getSigCalculoDetalheConsumoScoMaterialCodigo());
				if (consultaC6 != null) {
					for (SigCalculoAtendimentoPacienteVO voC6 : consultaC6) {
						SigCalculoDetalheConsumo detalheConsumo = this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().obterPorChavePrimaria(voC6.getSigCalculoDetalheConsumoSeq());
						// Passo 10
						// QTDE_CONSUMIDO = (QTDE_PREVISTO / QTDE_PREVISTO_TOTAL) * QTDE_DISPONIVEL_TOTAL
						detalheConsumo.setQtdeConsumido(ProcessamentoCustoUtils.dividir(voC6.getQuantidadePrevisto(),voC4.getQuantidadePrevistoTotal()).multiply(new BigDecimal(quantidadePrevistaTotal)));

						// VLR_CONSUMIDO = (VLR_PREVISTO / QTDE_PREVISTO) * QTDE_CONSUMIDO
						detalheConsumo.setValorConsumido(ProcessamentoCustoUtils.dividir(voC6.getValorPrevisto(),voC6.getQuantidadePrevisto()).multiply(detalheConsumo.getQtdeConsumido()));

						this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().atualizar(detalheConsumo);
						this.commitProcessamentoCusto();

					}
				}
			}
			// Passo 11
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.V, "INSUMO_VALORES_AJUSTADOS", voC4.getSigCalculoDetalheConsumoScoMaterialCodigo());

		}
	}

	private void consulta01(SigProcessamentoCusto sigProcessamentoCusto, List<SigCalculoAtendimentoPacienteVO> consultaC1) throws ApplicationBusinessException {
		for (SigCalculoAtendimentoPacienteVO voC1 : consultaC1) {
			// Consulta C2
			Object[] valorMedioInsumo = this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO()
					.buscarCustoMedioMaterialComUnidadeMedida(sigProcessamentoCusto.getSeq(), voC1.getSigCalculoAtdConsumoFccCentroCustoSeq(), voC1.getSigCalculoDetalheConsumoScoMaterialCodigo());

			BigDecimal valor = (BigDecimal)valorMedioInsumo[0];
			// Passo 03
			// Na consulta c1 não retorna o SEQ da SigCalculoDetalheConsumo -
			// por isso o SELECT a seguir om um FOR.:
			List<SigCalculoDetalheConsumo> listaDetalheConsumo = this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO() .obterPorSigCalculoAtdConsumo(voC1.getSigCalculoAtdConsumoSeq());
			for (SigCalculoDetalheConsumo detalheConsumo : listaDetalheConsumo) {

				detalheConsumo.setValorPrevisto(voC1.getSigCalculoDetalheConsumoQuantidadePrevisto().multiply(valor));
				detalheConsumo.setValorDebitado(voC1.getSigCalculoDetalheConsumoQuantidadeDebitado().multiply(valor));
				detalheConsumo.setValorConsumido(voC1.getSigCalculoDetalheConsumoQuantidadeConsumo().multiply(valor));

				this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().atualizar(detalheConsumo);
				this.commitProcessamentoCusto();
			}
		}
	}

	protected SigMvtoContaMensal criarSigMvtoContaMensal(SigProcessamentoCusto processamento, RapServidores servidor, FccCentroCustos centroCusto, ScoMaterial material, Long qtde, BigDecimal custoMedio, BigDecimal valor) {
		SigMvtoContaMensal sigMvtoContaMensal = new SigMvtoContaMensal();
		sigMvtoContaMensal.setSigProcessamentoCustos(processamento);
		sigMvtoContaMensal.setCriadoEm(new Date());
		sigMvtoContaMensal.setRapServidores(servidor);
		sigMvtoContaMensal.setTipoMvto(DominioTipoMovimentoConta.VRG);
		sigMvtoContaMensal.setTipoValor(DominioTipoValorConta.DI);
		sigMvtoContaMensal.setFccCentroCustos(centroCusto);
		sigMvtoContaMensal.setScoMaterial(material);
		sigMvtoContaMensal.setQtde(qtde);
		sigMvtoContaMensal.setCustoMedio(custoMedio);
		sigMvtoContaMensal.setValor(valor);
		return sigMvtoContaMensal;
	}
}
