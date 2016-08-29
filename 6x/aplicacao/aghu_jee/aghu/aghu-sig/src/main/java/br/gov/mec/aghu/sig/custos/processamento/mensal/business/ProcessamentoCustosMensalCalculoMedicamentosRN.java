package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * #32236 – Efetuar cálculo de custo de medicamentos
 * 
 * @author fpalma
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalCalculoMedicamentosRN.class)
public class ProcessamentoCustosMensalCalculoMedicamentosRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = 7697734805190056541L;

    public enum ProcessamentoCustosMensalCalculoMedicamentosRNExceptionCode implements BusinessExceptionCode {
        SCE_00863
    }

	@Override
	public String getTitulo() {
		return "Cálculo de custo de medicamentos";
	}

	@Override
	public String getNome() {
		return "ProcessamentoCustosMensalCalculoMedicamentosRN";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 38;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos, DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {

		// Passo 1
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores,sigProcessamentoPassos, DominioEtapaProcessamento.V, "INICIO_PROCESSAMENTO_MEDICAMENTOS_DIALISE");

		// Passo 2
		
		//A consulta original do documento é a SigCalculoAtdPacienteDAO->buscarMedicamentos, mas a sua execução demorava cerca de 15 minutos
		//Em conjunto com o analista, fiz alterações para quebrar a consulta em outras menores para otimizar o uso dos dados
		SigCategoriaConsumos categoriaConsumo = this.getProcessamentoCustoUtils().getSigCategoriaConsumosDAO().obterCategoriaConsumoPorIndicadorContagem(DominioIndContagem.MD);
		List<SigCalculoAtdConsumo> listaConsumo =  this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarConsumos(sigProcessamentoCusto.getSeq(), categoriaConsumo.getSeq());
		
		//Busca parâmetros utilizados nas consultas
		Short tipoMovimentoNotaRecebimento = null;
        try {
        	AghParametros aghParametro = this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_NR);
        	tipoMovimentoNotaRecebimento = aghParametro.getVlrNumerico().shortValue();
        } catch (BaseException e) {
            throw new ApplicationBusinessException(ProcessamentoCustosMensalCalculoMedicamentosRNExceptionCode.SCE_00863);
        }
        
        AghParametros parametro = this.getProcessamentoCustoUtils().getParametroFacade().obterAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
        Integer numeroFornecedor = parametro.getVlrNumerico().intValue();

        List<SigCalculoDetalheConsumo> listaDetalhe = null;
        Map<Integer, Double> listaCustoMedicamento = new HashMap<Integer, Double>();
        Map<String, BigDecimal> listaCustoMedioInsumo = new HashMap<String, BigDecimal>();
        Double valorCustoMedicamento = null;
        BigDecimal valorCustoMedicamentoBigDecimal = null;
        
        // Passo 3
        for(SigCalculoAtdConsumo consumo : listaConsumo){ //Para cada consumo dos medicamentos	deve buscar os detalhes
        	listaDetalhe = this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().obterPorSigCalculoAtdConsumo(consumo.getSeq());
        	    	
        	for(SigCalculoDetalheConsumo detalheConsumo : listaDetalhe){// e para cada detalhe executar os passos descritos no documento de análise
        		
        		valorCustoMedicamento = this.buscarCustoMedicamento(detalheConsumo.getProcedHospInterno().getMaterial().getCodigo(), detalheConsumo.getProcedHospInterno().getSeq(), listaCustoMedicamento, tipoMovimentoNotaRecebimento, numeroFornecedor, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
        		valorCustoMedicamentoBigDecimal = new BigDecimal(valorCustoMedicamento);
				
        		// Passo 5
                detalheConsumo.setValorPrevisto(detalheConsumo.getQtdePrevisto().multiply(valorCustoMedicamentoBigDecimal));
                detalheConsumo.setValorDebitado(detalheConsumo.getQtdeDebitado().multiply(valorCustoMedicamentoBigDecimal));
                detalheConsumo.setValorConsumido(detalheConsumo.getQtdeConsumido().multiply(valorCustoMedicamentoBigDecimal));
        		this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().atualizar(detalheConsumo);
        		
				// Passo 6
        		consumo.setValorParcialPrevistoInsumos(consumo.getValorParcialPrevistoInsumos().add(detalheConsumo.getValorConsumido()));
        		consumo.setValorParcialConsumidoInsumos(consumo.getValorParcialConsumidoInsumos().add(detalheConsumo.getValorConsumido()));
        		this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().atualizar(consumo);

				// Passo 7
	            this.inserirContaMensal(consumo, detalheConsumo, listaCustoMedioInsumo, sigProcessamentoCusto, rapServidores);
	            
	        	// Passo 8
				this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,
						sigProcessamentoPassos, DominioEtapaProcessamento.V, "VALOR_CONSUMIDO_MEDICAMENTO_CALCULADO",detalheConsumo.getProcedHospInterno().getMaterial().getCodigo());
        	}
        	this.commitProcessamentoCusto();
		}
	}
	
	private void inserirContaMensal(SigCalculoAtdConsumo consumo,  SigCalculoDetalheConsumo detalheConsumo, Map<String, BigDecimal> listaCustoMedioInsumo, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores) throws ApplicationBusinessException {
		SigMvtoContaMensal contaMensal = new SigMvtoContaMensal();
		contaMensal.setSigProcessamentoCustos(sigProcessamentoCusto);
		contaMensal.setRapServidores(rapServidores);
		contaMensal.setCriadoEm(new Date());
		contaMensal.setTipoMvto(DominioTipoMovimentoConta.VRG);
		contaMensal.setTipoValor(DominioTipoValorConta.DI);
		contaMensal.setFccCentroCustos(consumo.getCentroCustos());
		contaMensal.setScoMaterial(detalheConsumo.getProcedHospInterno().getMaterial());
		contaMensal.setQtde(detalheConsumo.getQtdeConsumido().longValue());
		contaMensal.setCustoMedio(this.buscarCustoMedioInsumo(consumo, detalheConsumo, listaCustoMedioInsumo, sigProcessamentoCusto));
		contaMensal.setValor(detalheConsumo.getValorConsumido().multiply(new BigDecimal(-1)));

		this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().persistir(contaMensal);
	}
	
	private Double buscarCustoMedicamento(Integer codigoMaterial, Integer phiSeq, Map<Integer, Double> listaCustoMedicamento, Short tipoMovimentoNotaRecebimento,  Integer numeroFornecedor,  SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos){
		
		
		if(!listaCustoMedicamento.containsKey(codigoMaterial)){
			// Passo 4
	        List<Double> custoMedicamentoEstoque = this.getProcessamentoCustoUtils().getEstoqueFacade()
	                .buscarCustoMedicamentoPelaUltimaEntradaEstoque(codigoMaterial, sigProcessamentoCusto.getDataInicio(), tipoMovimentoNotaRecebimento);
	        
	        Double vlrInsumo = null;
	        //RN01
	        if(custoMedicamentoEstoque != null && !custoMedicamentoEstoque.isEmpty()  && !custoMedicamentoEstoque.get(0).equals(Double.valueOf(0))) {
	            vlrInsumo = custoMedicamentoEstoque.get(0);
	        } else {
	            Double custoMedioPonderado = this.getProcessamentoCustoUtils().getEstoqueFacade().getCustoMedioPonderado(codigoMaterial, sigProcessamentoCusto.getDataInicio(), numeroFornecedor);
	            if(Double.valueOf(0).equals(custoMedioPonderado)) {
	                this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,
	                        sigProcessamentoPassos, DominioEtapaProcessamento.V, "MENSAGEM_MEDICAMENTO_SEM_NOTA_FISCAL", phiSeq);
	            }
	            vlrInsumo = custoMedioPonderado;
	        }
	        
	        listaCustoMedicamento.put(codigoMaterial, vlrInsumo);
		}

        return listaCustoMedicamento.get(codigoMaterial);
	}

	
	private BigDecimal buscarCustoMedioInsumo(SigCalculoAtdConsumo consumo,  SigCalculoDetalheConsumo detalheConsumo, Map<String, BigDecimal> listaCustoMedioInsumo, SigProcessamentoCusto sigProcessamentoCusto){
		
		String chave = consumo.getCentroCustos().getCodigo() + "-" + detalheConsumo.getProcedHospInterno().getMaterial().getCodigo();
		if(!listaCustoMedioInsumo.containsKey(chave)){
		
			BigDecimal custoMedio = this.getProcessamentoCustoUtils().getSigAtividadeInsumosDAO().buscaDetalheInsumosConsumidos(sigProcessamentoCusto.getSeq(), consumo.getCentroCustos().getCodigo(), detalheConsumo.getProcedHospInterno().getMaterial().getCodigo());
			
			if(custoMedio == null){
				custoMedio = BigDecimal.ZERO;
			}
			listaCustoMedioInsumo.put(chave, custoMedio);
		}
		return listaCustoMedioInsumo.get(chave);
	}
}
