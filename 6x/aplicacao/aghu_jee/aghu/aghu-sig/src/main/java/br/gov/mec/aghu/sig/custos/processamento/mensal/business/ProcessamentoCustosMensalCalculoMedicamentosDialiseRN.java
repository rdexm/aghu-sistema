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
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.SigCalculoAtdConsumo;
import br.gov.mec.aghu.model.SigCalculoDetalheConsumo;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoExceptionCode;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.MedicamentosDialiseVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * #32267 – Efetuar cÁlculo de custo de medicamentos de diálise
 * 
 * @author fpalma
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalCalculoMedicamentosDialiseRN.class)
public class ProcessamentoCustosMensalCalculoMedicamentosDialiseRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = 7697734805190056541L;

    public enum ProcessamentoCustosMensalCalculoMedicamentosDialiseRNExceptionCode implements BusinessExceptionCode {
        SCE_00863
    }

	@Override
	public String getTitulo() {
		return "Cálculo de custo de medicamentos de diálise";
	}

	@Override
	public String getNome() {
		return "ProcessamentoCustosMensalCalculoMedicamentosDialiseRN";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 37;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos, DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {

		// Passo 1
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,sigProcessamentoPassos, DominioEtapaProcessamento.V, "INICIO_PROCESSAMENTO_MEDICAMENTOS_DIALISE");

		// Passo 2
		List<MedicamentosDialiseVO> consultaPrincipal = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().buscarMedicamentosDialise(sigProcessamentoCusto.getSeq(), Boolean.FALSE, DominioIndContagem.DM);
		
		Short tmvSeq = 0;
        try {
        	 AghParametros aghParametro = this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_NR);
        	 tmvSeq = aghParametro.getVlrNumerico().shortValue();
        } catch (BaseException e) {
             throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_BUSCA_PARAMETROS, "P_TMV_DOC_NR");
        }
         
         Integer frnNumero;
         try {
        	 AghParametros parametro = this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
        	 frnNumero = parametro.getVlrNumerico().intValue();
         } catch (BaseException e) {
             throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_BUSCA_PARAMETROS, "P_FORNECEDOR_PADRAO");
         }
		
         SigCalculoDetalheConsumo  detalheConsumo;
         Double vlrInsumo = null;
         List<Double> custoMedicamentoEstoque;
         FccCentroCustos centroCusto;
         ScoMaterial material;
         BigDecimal custoMedio;
         
		// Passo 3
		for (MedicamentosDialiseVO vo : consultaPrincipal) {

			// Passo 4
            custoMedicamentoEstoque = this.getProcessamentoCustoUtils().getEstoqueFacade().buscarCustoMedicamento(vo.getMatCodigo(), sigProcessamentoCusto.getCompetencia(), tmvSeq);
            
            //RN01
            if(custoMedicamentoEstoque != null && !custoMedicamentoEstoque.isEmpty() && custoMedicamentoEstoque.get(0)!= null && !custoMedicamentoEstoque.get(0).equals(Double.valueOf(0))) {
                vlrInsumo = custoMedicamentoEstoque.get(0);
            } else {
                vlrInsumo = this.getProcessamentoCustoUtils().getEstoqueFacade().getCustoMedioPonderado(vo.getMatCodigo(), sigProcessamentoCusto.getCompetencia(), frnNumero);
                
                if(vlrInsumo.equals(0)) {
                    this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,sigProcessamentoPassos, DominioEtapaProcessamento.V, "MENSAGEM_MEDICAMENTO_SEM_NOTA_FISCAL", vo.getPhiSeq().toString());
                }
            }

			// Passo 5
			detalheConsumo = this.atualizarDetalheConsumo(vo.getCdcSeq(), vlrInsumo);

			// Passo 6
			this.atualizarConsumo(vo.getCcaSeq(), detalheConsumo.getValorConsumido());

			custoMedio = this.getProcessamentoCustoUtils().getSigAtividadeInsumosDAO().buscaDetalheInsumosConsumidos(sigProcessamentoCusto.getSeq(), vo.getCctCodigo(), vo.getMatCodigo());
			centroCusto = this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(vo.getCctCodigo());
			material = this.getProcessamentoCustoUtils().getComprasFacade().obterScoMaterialPorChavePrimaria(vo.getMatCodigo());
			
			// Passo 7
            this.inserirContaMensal(detalheConsumo.getValorConsumido(), vo.getQtdeConsumido(), custoMedio, centroCusto, material,  rapServidores, sigProcessamentoCusto);

			// Passo 8
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,sigProcessamentoPassos, DominioEtapaProcessamento.V, "VALOR_CONSUMIDO_MEDICAMENTO_DIALISE_CALCULADO", vo.getMatCodigo());
			
			this.commitProcessamentoCusto();
		}
	}

	private SigCalculoDetalheConsumo atualizarDetalheConsumo(Integer cdcSeq, Double vlrInsumo) {

		SigCalculoDetalheConsumo detalheConsumo = this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().obterPorChavePrimaria(cdcSeq);

		BigDecimal valorInsumo = new BigDecimal(vlrInsumo);
        
		// Passo 6
        detalheConsumo.setValorPrevisto(detalheConsumo.getQtdePrevisto().multiply(valorInsumo));
        detalheConsumo.setValorDebitado(detalheConsumo.getQtdeDebitado().multiply(valorInsumo));
        detalheConsumo.setValorConsumido(detalheConsumo.getQtdeConsumido().multiply(valorInsumo));
		this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().atualizar(detalheConsumo);
		return detalheConsumo;
	}
	
	private void atualizarConsumo(Integer ccaSeq, BigDecimal valorConsumido) {
		SigCalculoAtdConsumo consumo = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().obterPorChavePrimaria(ccaSeq);
		consumo.setValorParcialPrevistoInsumos(consumo.getValorParcialPrevistoInsumos().add(valorConsumido));
		consumo.setValorParcialConsumidoInsumos(consumo.getValorParcialConsumidoInsumos().add(valorConsumido));
		this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().atualizar(consumo);
	}

	private void inserirContaMensal(BigDecimal valorConsumido, BigDecimal quantidadeConsumida, BigDecimal custoMedio, FccCentroCustos centroCusto, ScoMaterial material, RapServidores rapServidores, SigProcessamentoCusto sigProcessamentoCusto) {
		SigMvtoContaMensal contaMensal = new SigMvtoContaMensal();
		contaMensal.setSigProcessamentoCustos(sigProcessamentoCusto);
		contaMensal.setRapServidores(rapServidores);
		contaMensal.setCriadoEm(new Date());
		contaMensal.setTipoMvto(DominioTipoMovimentoConta.VRG);
		contaMensal.setTipoValor(DominioTipoValorConta.DI);
		contaMensal.setFccCentroCustos(centroCusto);
		contaMensal.setScoMaterial(material);
		contaMensal.setQtde(quantidadeConsumida.longValue());
		contaMensal.setCustoMedio(custoMedio);
		contaMensal.setValor(valorConsumido.multiply(new BigDecimal(-1)));
		this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().persistir(contaMensal);
	}
}
