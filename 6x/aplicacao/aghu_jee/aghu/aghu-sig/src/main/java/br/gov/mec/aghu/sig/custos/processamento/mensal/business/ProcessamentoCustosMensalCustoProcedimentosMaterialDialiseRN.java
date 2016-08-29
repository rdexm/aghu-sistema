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
import br.gov.mec.aghu.dominio.DominioIndContagem;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoConta;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoValorConta;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoAtdConsumo;
import br.gov.mec.aghu.model.SigCalculoDetalheConsumo;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.MedicamentosDialiseVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * #32268 – Efetuar Cálculo de custo de procedimentos e materiais de diálise
 * 
 * @author fpalma
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalCustoProcedimentosMaterialDialiseRN.class)
public class ProcessamentoCustosMensalCustoProcedimentosMaterialDialiseRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = 7697734805190056541L;

    public enum ProcessamentoCustosMensalCustoProcedimentosMaterialDialiseRNRNExceptionCode implements BusinessExceptionCode {
        SCE_00863
    }

	@Override
	public String getTitulo() {
		return "Cálculo de custo de procedimentos e materiais de diálise";
	}

	@Override
	public String getNome() {
		return "ProcessamentoCustosMensalCustoProcedimentosMaterialDialiseRN";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 39;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos, DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {

		// Passo 1
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,
				sigProcessamentoPassos, DominioEtapaProcessamento.V, "INICIO_PROCESSAMENTO_PROCEDIMENTOS_MATERIAIS_DIALISE");

		// Passo 2
		List<MedicamentosDialiseVO> consultaPrincipal = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO()
				.buscarMedicamentosDialise(sigProcessamentoCusto.getSeq(), Boolean.TRUE, DominioIndContagem.DP, DominioIndContagem.CR );
        consultaPrincipal.addAll(this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO()
                .buscarMedicamentosDialisePhi(sigProcessamentoCusto.getSeq(), DominioIndContagem.DP, DominioIndContagem.CR));

        executar(consultaPrincipal, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);

	}

	private void executar(List<MedicamentosDialiseVO> consultaPrincipal, SigProcessamentoCusto sigProcessamentoCusto,
			RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {
		// Passo 3
		for (MedicamentosDialiseVO vo : consultaPrincipal) {
            AghParametros aghParametro;
            try {

                aghParametro = this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_NR);
            } catch (BaseException e) {
                throw new ApplicationBusinessException(ProcessamentoCustosMensalCustoProcedimentosMaterialDialiseRNRNExceptionCode.SCE_00863);
            }

            AghParametros parametro = this.getProcessamentoCustoUtils().getParametroFacade().obterAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);//(nome)buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_HCPA);
            Integer frnNumero = parametro.getVlrNumerico().intValue();

			// Passo 4
            Double vlrInsumo = Double.valueOf(0);

            List<Double> custoMaterial = this.getProcessamentoCustoUtils().getEstoqueFacade().buscarCustoMedicamento(vo.getMatCodigo(),
                    sigProcessamentoCusto.getCompetencia(), aghParametro.getVlrNumerico().shortValue());

            //RN01
            if(custoMaterial != null && !custoMaterial.isEmpty() && custoMaterial.get(0) != null
                    && !custoMaterial.get(0).equals(Double.valueOf(0))) {
                vlrInsumo = custoMaterial.get(0);
            } else {
                List<BigDecimal> custoMedioPonderado = this.getProcessamentoCustoUtils().getEstoqueFacade()
                        .obterCustoMedioPonderadoDoMaterialEstoqueGeral(vo.getMatCodigo(), sigProcessamentoCusto.getCompetencia(), frnNumero);
                if(custoMedioPonderado.isEmpty()) {
                    this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,
                            sigProcessamentoPassos, DominioEtapaProcessamento.V, "MENSAGEM_MEDICAMENTO_SEM_NOTA_FISCAL", vo.getPhiSeq().toString());
                } else {
                    vlrInsumo = Double.valueOf(custoMedioPonderado.get(0).toString());
                }
            }

			// Passo 5
			SigCalculoDetalheConsumo detalheConsumo = atualizarSigCalculoDetalheConsumo(vo, vlrInsumo);

			// Passo 6
			atualizarCalculoConsumo(vo, detalheConsumo);

			// Passo 7
            inserirContaMensal(vo, rapServidores, detalheConsumo, sigProcessamentoCusto);

			// Passo 8
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,
					sigProcessamentoPassos, DominioEtapaProcessamento.V, "VALOR_CONSUMIDO_PROCEDIMENTOS_MATERIAIS_DIALISE_CALCULADO",
					vo.getMatCodigo().toString());
		}
	}

	private void atualizarCalculoConsumo(MedicamentosDialiseVO vo, SigCalculoDetalheConsumo detalheConsumo) throws ApplicationBusinessException {
		SigCalculoAtdConsumo consumo = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO()
				.obterPorChavePrimaria(vo.getCcaSeq());
		consumo.setValorParcialPrevistoInsumos(consumo.getValorParcialPrevistoInsumos().add(detalheConsumo.getValorConsumido()));
		consumo.setValorParcialConsumidoInsumos(consumo.getValorParcialConsumidoInsumos().add(detalheConsumo.getValorConsumido()));
		this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().atualizar(consumo);
		this.commitProcessamentoCusto();
	}

	private void inserirContaMensal(MedicamentosDialiseVO vo, RapServidores rapServidores, SigCalculoDetalheConsumo detalheConsumo,
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

		BigDecimal custoMedio = this.getProcessamentoCustoUtils().getSigAtividadeInsumosDAO()
                .buscaDetalheInsumosConsumidos(sigProcessamentoCusto.getSeq(), vo.getCctCodigo(), vo.getMatCodigo());

		contaMensal.setCustoMedio(custoMedio == null ? BigDecimal.ZERO : custoMedio);
		contaMensal.setValor(detalheConsumo.getValorConsumido().multiply(new BigDecimal(-1)));

		this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().persistir(contaMensal);
		this.commitProcessamentoCusto();
	}

	private SigCalculoDetalheConsumo atualizarSigCalculoDetalheConsumo(MedicamentosDialiseVO vo,
                Double vlrInsumo) throws ApplicationBusinessException {

		SigCalculoDetalheConsumo detalheConsumo = this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO()
				.obterPorChavePrimaria(vo.getCdcSeq());

        // Passo 6
		BigDecimal valor = new BigDecimal(vlrInsumo.toString());
        detalheConsumo.setValorPrevisto(detalheConsumo.getQtdePrevisto().multiply(valor));
        detalheConsumo.setValorDebitado(detalheConsumo.getQtdeDebitado().multiply(valor));
        detalheConsumo.setValorConsumido(detalheConsumo.getQtdeConsumido().multiply(valor));

		this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().atualizar(detalheConsumo);
		this.commitProcessamentoCusto();

		return detalheConsumo;
	}
}
