package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.hibernate.ScrollableResults;

import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoConta;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoValorConta;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.DebitoServicoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável por contabilizar os custos de contratos de serviços do mês por centro de custo.
 * 
 * #16834
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalContabilizacaoContratoServicoCCRN.class)
public class ProcessamentoCustosMensalContabilizacaoContratoServicoCCRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = -5811664870339719887L;

	@Override
	public String getTitulo() {
		return "Contabilizacao dos custos de contratos de servicos do mes por centro de custo.";
	}

	@Override
	public String getNome() {
		return "processamentoMensalContabilizacaoContratoServicoCCON - processamentoContratoServicoCentroCusto";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 5;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		this.processamentoContratoServicoCentroCusto(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
	}

	/**
	 * Método principal da Etapa 5 do processamento, é a responsável em disparar todos os passos dessa etapa.
	 * 
	 * @author rmalvezzi
	 * @param sigProcessamentoCusto					Processamento Atual.
	 * @param rapServidores							Servidor Logado.
	 * @param sigProcessamentoPassos				Passo Atual.
	 * @throws ApplicationBusinessException		Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 */
	public void processamentoContratoServicoCentroCusto(SigProcessamentoCusto processamentoCusto, RapServidores servidor,
			SigProcessamentoPassos processamentoPassos) throws ApplicationBusinessException {

		//Passo 01
		this.buscarMensagemEGravarLogProcessamento(processamentoCusto, servidor, processamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_INICIO_CONTABILIZACAO_SERVICOS");

		//Passo 02
		ScrollableResults retorno = this.getProcessamentoCustoUtils().getSigProcessamentoCustoDAO().buscarDebitoServicos(processamentoCusto);

		//Passo 03
		
		this.buscarMensagemEGravarLogProcessamentoSemCommit(processamentoCusto, servidor, processamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_SUCESSO_BUSCA_CONTABILIZACAO_SERVICOS");

		// passo 04
		if (retorno != null) {
			while (retorno.next()) {

				DebitoServicoVO debitoServicoVO = new DebitoServicoVO((Object[]) retorno.get());

				SigMvtoContaMensal mvtoContaMensal = new SigMvtoContaMensal();

				mvtoContaMensal.setSigProcessamentoCustos(processamentoCusto);
				mvtoContaMensal.setCriadoEm(new Date());
				mvtoContaMensal.setRapServidores(servidor);
				mvtoContaMensal.setTipoMvto(DominioTipoMovimentoConta.SIP);
				mvtoContaMensal.setTipoValor(DominioTipoValorConta.DS);
				mvtoContaMensal.setFccCentroCustos(this.getProcessamentoCustoUtils().getCentroCustoFacade()
						.obterCentroCustoPorChavePrimaria(debitoServicoVO.getCctCodigoAplic()));

				if (debitoServicoVO.getCctCodigoAplic() != debitoServicoVO.getCctCodigo()) {
					mvtoContaMensal.setFccCentroCustosDebita(this.getProcessamentoCustoUtils().getCentroCustoFacade()
							.obterCentroCustoPorChavePrimaria(debitoServicoVO.getCctCodigo()));
				}

				mvtoContaMensal.setSolicitacaoServico(this.getProcessamentoCustoUtils().getComprasFacade()
						.obterDescricaoSolicitacaoServicoPeloId(debitoServicoVO.getSlsNumero()));

				mvtoContaMensal.setServico(this.getProcessamentoCustoUtils().getComprasFacade().obterServicoPorId(debitoServicoVO.getSrvCodigo()));

				//RN03 - Busca item de contrato da AF (se não existir, vai retorna nulo). Deve usar o IafAfnNumero mesmo
				mvtoContaMensal.setScoAfContrato(this.getProcessamentoCustoUtils().getSiconFacade()
						.buscaItemContratoAF(debitoServicoVO.getIafAfnNumero()));

				mvtoContaMensal.setAutorizacaoForn(this.getProcessamentoCustoUtils().getAutFornecimentoFacade()
						.obterAfByNumero(debitoServicoVO.getIafAfnNumero()));

				mvtoContaMensal.setQtde(1L);
				mvtoContaMensal.setValor(new BigDecimal(debitoServicoVO.getValor()));

				this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().persistir(mvtoContaMensal);
			}
			retorno.close();
			this.commitProcessamentoCusto();
		}

		//Passo 05
		this.buscarMensagemEGravarLogProcessamento(processamentoCusto, servidor, processamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_SUCESSO_ETAPA5_CONTABILIZACAO_SERVICOS");
	}
}
