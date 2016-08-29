package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoConta;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoValorConta;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigCalculoRateioEquipamento;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.DepreciacaoEquipamentoRateioVO;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoProducaoRateioVO;

/**
 * #21004  - Rateio custos diretos de equipamentos dos centros de custos não associados a atividades de objetos de custo 
 * 
 * @author rhrosa
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalRateioDiretoEquipamentoRN.class)
public class ProcessamentoCustosMensalRateioDiretoEquipamentoRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = -7616893702933974426L;

	@Override
	public String getTitulo() {
		return "Processar rateio custos diretos - equipamentos.";
	}

	@Override
	public String getNome() {
		return "processamentoRateioCustoDiretoEquipamentoRN - processamentoRateioEquipamentos";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 17;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		this.processarRateioEquipamentos(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
	}

	/**
	 * Fluxo principal do processamento de Rateio de consumos diretos de equipamentos
	 * 
	 * @author rhrosa
	 * @param sigProcessamentoCusto					Processamento Atual.
	 * @param rapServidores							Servidor Logado.
	 * @param sigProcessamentoPassos				Passo Atual.
	 * @throws ApplicationBusinessException		Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 */
	private void processarRateioEquipamentos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {

		//Passo 1
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.E, "MENSAGEM_INICIO_RATEIO_EQUIPAMENTOS");

		//Passo 2
		List<DepreciacaoEquipamentoRateioVO> lista = this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO()
				.buscarDepreciacaoRateioEquipamentos(sigProcessamentoCusto.getSeq());

		//Passo 3
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.E, "MENSAGEM_SUCESSO_BUSCA_EQUIPAMENTOS_PARA_RATEIO");

		if (ProcessamentoCustoUtils.verificarListaNaoVazia(lista)) {
			
			FccCentroCustos centroCustoCorrente = null;
			boolean encontrouObjetoCustoParaRateioNoCentroCusto = false;
			List<ObjetoCustoProducaoRateioVO>  objetosCustosProducaoRateio = null;
			
			//Passo 10
			for(DepreciacaoEquipamentoRateioVO depreciacaoVO : lista){

				if (centroCustoCorrente == null || !depreciacaoVO.getCctCodigo().equals(centroCustoCorrente.getCodigo())) {
					centroCustoCorrente = this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(depreciacaoVO.getCctCodigo());
					encontrouObjetoCustoParaRateioNoCentroCusto = false;
					
					//Passo 4		
					objetosCustosProducaoRateio = this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().buscarObjetosCustoComProducaoParaRateio(sigProcessamentoCusto.getSeq(), centroCustoCorrente.getCodigo());
				}

				if (!encontrouObjetoCustoParaRateioNoCentroCusto) {
					
					if (ProcessamentoCustoUtils.verificarListaNaoVazia(objetosCustosProducaoRateio)) {
						
						//Passo 7
						for(ObjetoCustoProducaoRateioVO objetoCustoVO : objetosCustosProducaoRateio){

							//Passo 5
							SigCalculoRateioEquipamento sigCalculoRateioEquipamento = new SigCalculoRateioEquipamento();
							sigCalculoRateioEquipamento.setCriadoEm(new Date());
							sigCalculoRateioEquipamento.setRapServidores(rapServidores);

							SigCalculoObjetoCusto sigCalculoObjetoCusto = this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().obterPorChavePrimaria(objetoCustoVO.getCbjSeq());

							sigCalculoRateioEquipamento.setSigCalculoObjetoCustos(sigCalculoObjetoCusto);
							sigCalculoRateioEquipamento.setCodPatrimonio(depreciacaoVO.getCodPatrimonio());

							sigCalculoRateioEquipamento.setQtde(ProcessamentoCustoUtils.dividir(objetoCustoVO.getPesoOc(),objetoCustoVO.getSomaPesos()).doubleValue());

							BigDecimal vlrDepreciacao = ProcessamentoCustoUtils.dividir(depreciacaoVO.getVlrDepreciacao() ,objetoCustoVO.getSomaPesos()).multiply(objetoCustoVO.getPesoOc());

							sigCalculoRateioEquipamento.setVlrDepreciacao(vlrDepreciacao);
							sigCalculoRateioEquipamento.setPeso(objetoCustoVO.getPesoOc());

							this.getProcessamentoCustoUtils().getSigCalculoRateioEquipamentoDAO().persistir(sigCalculoRateioEquipamento);

							//Passo 6
							sigCalculoObjetoCusto.setVlrRateioEquipamentos(sigCalculoObjetoCusto.getVlrRateioEquipamentos().add(vlrDepreciacao));
							this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().atualizar(sigCalculoObjetoCusto);

							//Passo 8
							SigMvtoContaMensal sigMvtoContaMensal = new SigMvtoContaMensal();
							sigMvtoContaMensal.setSigProcessamentoCustos(sigProcessamentoCusto);
							sigMvtoContaMensal.setCriadoEm(new Date());
							sigMvtoContaMensal.setRapServidores(rapServidores);
							sigMvtoContaMensal.setTipoMvto(DominioTipoMovimentoConta.VAR);
							sigMvtoContaMensal.setTipoValor(DominioTipoValorConta.DE);
							sigMvtoContaMensal.setFccCentroCustos(centroCustoCorrente);
							sigMvtoContaMensal.setCodPatrimonio(depreciacaoVO.getCodPatrimonio());
							sigMvtoContaMensal.setQtde(Long.valueOf(1));
							sigMvtoContaMensal.setValor(depreciacaoVO.getVlrDepreciacao().negate());

							this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().persistir(sigMvtoContaMensal);							

							//Passo 9
							this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.E, "MENSAGEM_SUCESSO_RATEIO_EQUIPAMENTO", depreciacaoVO.getCodPatrimonio());
							
							this.commitProcessamentoCusto();
						} 
					}
					else {
						//[FE01]							
						this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.E, "MENSAGEM_CENTRO_CUSTO_NAO_POSSUI_OBJETO_CUSTO", depreciacaoVO.getCctCodigo(), sigProcessamentoCusto.getCompetenciaMesAno());
						encontrouObjetoCustoParaRateioNoCentroCusto = true;
					}
				}
			}			
		}

		//Passo 11
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.E, "MENSAGEM_SUCESSO_CALCULO_RATEIO_EQUIPAMENTOS");
	}
}
