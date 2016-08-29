package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoConta;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoValorConta;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoAtdConsumo;
import br.gov.mec.aghu.model.SigCalculoDetalheConsumo;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.ContagemOrtesesProtesesVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * #32253 – EFETUAR CÁLCULO DE CUSTO DE ÓRTESES E PRÓTESES
 * 
 * @author rogeriovieira
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalCalculoOrtesesEProtesesRN.class)
public class ProcessamentoCustosMensalCalculoOrtesesEProtesesRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = 7697734805190056541L;

	@Override
	public String getTitulo() {
		return "Cálculo de custo de órteses e próteses";
	}

	@Override
	public String getNome() {
		return "ProcessamentoCustosMensalCalculoOrtesesEProtesesRN";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 34;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos, DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {

		// Passo 1
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,
				sigProcessamentoPassos, DominioEtapaProcessamento.V, "INICIO_PROCESSAMENTO_ORTESES_PROTESES");

		// Passo 2
		List<ContagemOrtesesProtesesVO> consultaPrincipal = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO()
				.buscarOrtesesEProtesesUtilizadas(sigProcessamentoCusto.getSeq());

		executar(consultaPrincipal, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);

	}

	private void executar(List<ContagemOrtesesProtesesVO> consultaPrincipal, SigProcessamentoCusto sigProcessamentoCusto,
			RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {
		// Passo 3
		for (ContagemOrtesesProtesesVO vo : consultaPrincipal) {
			// Passo 4
			Double valorUnitario = this.getProcessamentoCustoUtils().getEstoqueFacade()
					.verificarDoacoes(vo.getMatCodigo(), vo.getRmpSeq(), Boolean.FALSE);
			Boolean retornouResultadoPasso4 = valorUnitario != null;

			// Pedaço do passo 6
			if (!retornouResultadoPasso4) {
				valorUnitario = processarValorUnitario(vo, valorUnitario, sigProcessamentoCusto, sigProcessamentoPassos, rapServidores);
			}

			// Passo 5 ou 6
			SigCalculoDetalheConsumo detalheConsumo = atualizarSigCalculoDetalheConsumo(vo, retornouResultadoPasso4, valorUnitario);

			// Passo 07
			atualizarCalculoConsumo(vo, detalheConsumo);

			// Passo 8
			inserirContaMenal(vo, rapServidores, detalheConsumo, sigProcessamentoCusto);

			// Passo 9
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,
					sigProcessamentoPassos, DominioEtapaProcessamento.V, "VALOR_CONSUMIDO_ORTESE_PROTESE_CALCULADO",
					vo.getMatCodigo().toString());
		}
	}

	private void atualizarCalculoConsumo(ContagemOrtesesProtesesVO vo, SigCalculoDetalheConsumo detalheConsumo) throws ApplicationBusinessException {
		SigCalculoAtdConsumo consumo = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO()
				.obterPorChavePrimaria(vo.getCcaSeq());
		consumo.setValorParcialPrevistoInsumos(consumo.getValorParcialPrevistoInsumos().add(detalheConsumo.getValorConsumido()));
		consumo.setValorParcialConsumidoInsumos(consumo.getValorParcialConsumidoInsumos().add(detalheConsumo.getValorConsumido()));
		this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().atualizar(consumo);
		this.commitProcessamentoCusto();
	}

	private void inserirContaMenal(ContagemOrtesesProtesesVO vo, RapServidores rapServidores, SigCalculoDetalheConsumo detalheConsumo,
			SigProcessamentoCusto sigProcessamentoCusto) throws ApplicationBusinessException {
		SigMvtoContaMensal contaMensal = new SigMvtoContaMensal();
		contaMensal.setSigProcessamentoCustos(sigProcessamentoCusto);
		contaMensal.setRapServidores(rapServidores);
		contaMensal.setCriadoEm(new Date());
		contaMensal.setTipoMvto(DominioTipoMovimentoConta.VRG);
		contaMensal.setTipoValor(DominioTipoValorConta.DI);
		contaMensal.setFccCentroCustos(getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(vo.getCctCodigo()));
		contaMensal.setScoMaterial(this.getProcessamentoCustoUtils().getComprasFacade().obterScoMaterialPorChavePrimaria(vo.getMatCodigo()));
		contaMensal.setQtde(vo.getQtdeConsumido().longValue());

		Double custoMedio = this.getProcessamentoCustoUtils().getEstoqueFacade().verificarDoacoes(vo.getMatCodigo(), vo.getRmpSeq(), Boolean.TRUE);

		contaMensal.setCustoMedio(new BigDecimal(custoMedio == null ? 0 : custoMedio));
		contaMensal.setValor(detalheConsumo.getValorConsumido().multiply(new BigDecimal(-1)));

		this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().persistir(contaMensal);
		this.commitProcessamentoCusto();
	}

	private Double processarValorUnitario(ContagemOrtesesProtesesVO vo, Double valorUnitario, SigProcessamentoCusto sigProcessamentoCusto,
			SigProcessamentoPassos sigProcessamentoPassos, RapServidores rapServidores) {

		valorUnitario = this.getProcessamentoCustoUtils().getEstoqueFacade()
				.verificarDoacoes(vo.getMatCodigo(), vo.getRmpSeq(), Boolean.TRUE);

		if (valorUnitario == null) {

			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,
					sigProcessamentoPassos, DominioEtapaProcessamento.V, "ORTESE_SEM_NOTA_FISCAL", vo.getPhiSeq(),
					vo.getRmpSeq());

			valorUnitario = this.getProcessamentoCustoUtils().getEstoqueFacade()
					.buscarCustoOrteseOuProteseUltimaEntrada(vo.getMatCodigo());

			if (valorUnitario == null) {
				this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,
						sigProcessamentoPassos, DominioEtapaProcessamento.V, "ORTESE_SEM_HISTORICO", vo.getPhiSeq(),
						vo.getRmpSeq());

				valorUnitario = 0d;
			}
		}

		return valorUnitario;
	}

	private SigCalculoDetalheConsumo atualizarSigCalculoDetalheConsumo(ContagemOrtesesProtesesVO vo, boolean atualizarCompleto,
			Double valorUnitario) throws ApplicationBusinessException {
		SigCalculoDetalheConsumo detalheConsumo = this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO()
				.obterPorChavePrimaria(vo.getCdcSeq());

		if (!atualizarCompleto) {
			BigDecimal valor = new BigDecimal(valorUnitario.toString());
			// Passo 6
			detalheConsumo.setValorPrevisto(detalheConsumo.getQtdePrevisto().multiply(valor));
			detalheConsumo.setValorDebitado(detalheConsumo.getQtdeDebitado().multiply(valor));
			detalheConsumo.setValorConsumido(detalheConsumo.getQtdeConsumido().multiply(valor));

		} else {
			detalheConsumo.setIndExterno(Boolean.TRUE);
		}

		this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().atualizar(detalheConsumo);
		this.commitProcessamentoCusto();

		return detalheConsumo;
	}
}
