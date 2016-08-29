package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

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
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoExceptionCode;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.EquipamentoProcessamentoMensalVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * Estoria do usuario 16834, 
 * 	-> Etapa 3 
 * 
 * Classe de execução da contabilização das depreciações dos equipamentos no mês por centro de custo.
 * 
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalContabilizacaoDepreciacaoEquipamentoRN.class)
public class ProcessamentoCustosMensalContabilizacaoDepreciacaoEquipamentoRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = -5811664870339719783L;

	@Override
	public String getTitulo() {
		return "Contabilizacao das depreciacoes dos equipamentos no mes por centro de custo.";
	}

	@Override
	public String getNome() {
		return "ProcessamentoCustoMensalContabilizacaoDepreciacaoEquipamentoON - processamentoInsumosEtapa3";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 3;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		this.processamentoInsumosEtapa3(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
	}

	/**
	 * Etapa que efetua a contabilização da depreciação dos equipamentos utilizados pelas atividades
	 * @param sigProcessamentoCusto - mês competencia do processamento 
	 * @param rapServidores - servidor responsavel
	 * @param sigProcessamentoPassos - passo do processamento mensal
	 * @throws ApplicationBusinessException - Exeção lançada caso ocorra algum erro
	 * @author jgugel
	 */
	private void processamentoInsumosEtapa3(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {	
		
		this.verificarServicoPatrimonioOnline();
		
		final Long qtdePadrao = 1L;

		//Passo 1
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.G, "MENSAGEM_CONTABILIZACAO_DEPRECIACAO_EQUIPAMENTO");

		//Passo 2
		List<EquipamentoProcessamentoMensalVO> listaEquipamentoDepreciado = this.getProcessamentoCustoUtils().getPatrimonioService()
				.buscaEquipamentosParaProcessamentoMensal(sigProcessamentoCusto.getCompetenciaMesAno());

		//Passo 3
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.G, "MENSAGEM_SUCESSO_BUSCA_DADOS_DEPRECIACAO_EQUIPAMENTO");

		//Passo 4
		for (EquipamentoProcessamentoMensalVO equipamentoProcessamentoMensalVo : listaEquipamentoDepreciado) {
			SigMvtoContaMensal contaMensal = new SigMvtoContaMensal();
			contaMensal.setCriadoEm(new Date());
			contaMensal.setSigProcessamentoCustos(sigProcessamentoCusto);
			contaMensal.setRapServidores(rapServidores);

			contaMensal.setTipoMvto(DominioTipoMovimentoConta.SIP);
			contaMensal.setTipoValor(DominioTipoValorConta.DE);
			contaMensal.setQtde(qtdePadrao);

			contaMensal.setFccCentroCustos(this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(equipamentoProcessamentoMensalVo.getCodCentroCusto()));
			contaMensal.setValor(equipamentoProcessamentoMensalVo.getTotalDepreciado());
			contaMensal.setCodPatrimonio(equipamentoProcessamentoMensalVo.getBem());

			this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().persistir(contaMensal);
		}

		this.commitProcessamentoCusto();

		//Passo 5
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.G, "MENSAGEM_SUCESSO_GRAVACAO_MOVIMENTOS_EQUIPAMENTOS");
	}
	
	private void verificarServicoPatrimonioOnline() throws ApplicationBusinessException{
		
		String idEquipamento = "0000"; Integer codigoCentroCusto = 0;
		try{
			this.getProcessamentoCustoUtils().getCustosSigFacade().pesquisarEquipamentoSistemaPatrimonioById(idEquipamento, codigoCentroCusto);
		}
		catch(Exception e1){
			//Não necessariamente está fora do ar, já que a primeira as vezes está dando SocketTimeoutException
			try{
				this.getProcessamentoCustoUtils().getCustosSigFacade().pesquisarEquipamentoSistemaPatrimonioById(idEquipamento, codigoCentroCusto);
			}
			catch(Exception e2){
				throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_SERVICO_PATRIMONIO, e1);
			}
		}
	}
}
