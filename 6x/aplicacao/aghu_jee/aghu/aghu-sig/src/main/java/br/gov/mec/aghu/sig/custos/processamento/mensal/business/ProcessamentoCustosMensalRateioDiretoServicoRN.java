package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.hibernate.ScrollableResults;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoConta;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoValorConta;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigCalculoRateioServico;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.CustoContratoServicoRateioVO;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoProducaoRateioVO;

/**
 * ETAPA 19: Processar rateio custos diretos - Serviços Estória de Usuário
 * #21006
 * 
 * @author rogeriovieira
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalRateioDiretoServicoRN.class)
public class ProcessamentoCustosMensalRateioDiretoServicoRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = 926721298285886272L;

	@Override
	public String getTitulo() {
		return "Processar rateio custos diretos - servicos.";
	}

	@Override
	public String getNome() {
		return "processamentoRateioCustoDiretoServicoRN - processamentoRateioServico";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 19;
	}

	@Override
	protected void executarPassosInternos(
			SigProcessamentoCusto sigProcessamentoCusto,
			RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto)
			throws ApplicationBusinessException {
		this.processarRateioServico(sigProcessamentoCusto, rapServidores,
				sigProcessamentoPassos);
	}

	/**
	 * Executa todos os passos do processamento da etapa 19
	 * 
	 * @author rogeriovieira
	 * @param sigProcessamentoCusto
	 *            Processamento atual.
	 * @param rapServidores
	 *            Servidor logado.
	 * @param sigProcessamentoPassos
	 *            Passo atual.
	 * @throws ApplicationBusinessException
	 *             Exceção lançada se alguma coisa aconteceu na hora do commit
	 *             do processamento.
	 */
	private void processarRateioServico(
			SigProcessamentoCusto sigProcessamentoCusto,
			RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos)
			throws ApplicationBusinessException {

		// Passo 1
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto,
						rapServidores, sigProcessamentoPassos,
						DominioEtapaProcessamento.S,
						"MENSAGEM_INICIO_RATEIO_SERVICOS");

		// Passo 2
		ScrollableResults retornoCustosContrato = this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().buscarCustosContrato(sigProcessamentoCusto.getSeq());

		// Passo 3
		this.buscarMensagemEGravarLogProcessamentoSemCommit(
						sigProcessamentoCusto, rapServidores,
						sigProcessamentoPassos, DominioEtapaProcessamento.S,
						"MENSAGEM_SUCESSO_BUSCA_SERVICOS_PARA_RATEIO");

		List<ObjetoCustoProducaoRateioVO> retornoObjetoCustoComProducaoParaRateio = null;

		Integer identifcadorContrato = null;
		ScoAfContrato afContrato = null;
		ScoAutorizacaoForn autorizacaoForn = null;
		ScoServico servico = null;

		// Variáveis utilizadas para verifica se o centro de custo possui
		// objetos de custo com produção
		List<Integer> centrosCustoSemObjetoCustoComProducao = new ArrayList<Integer>();

		while (retornoCustosContrato.next()) {

			identifcadorContrato = null;
			afContrato = null;
			autorizacaoForn = null;
			
			// Transforma a tupla retornada em um objeto
			CustoContratoServicoRateioVO contratoServicoVO = new CustoContratoServicoRateioVO(
					(Object[]) retornoCustosContrato.get());

			// Preenche os campos de acordo com o registro informado
			if (contratoServicoVO.getAfcoSeq() != null) {
				afContrato = this.getProcessamentoCustoUtils()
						.getSiconFacade()
						.obterAfContratosById(contratoServicoVO.getAfcoSeq());
				identifcadorContrato = contratoServicoVO.getAfcoSeq();
			} else if (contratoServicoVO.getAfNumero() != null) {
				autorizacaoForn = this.getProcessamentoCustoUtils()
						.getAutFornecimentoFacade()
						.obterAfByNumero(contratoServicoVO.getAfNumero());
				identifcadorContrato = contratoServicoVO.getAfNumero();
			}

			servico = this.getProcessamentoCustoUtils().getComprasFacade().obterServicoPorId(contratoServicoVO.getSrvCodigo());

			// [FE01] - Item 2 - Avancar todos equipamentos do centro de custo
			if (centrosCustoSemObjetoCustoComProducao.contains(contratoServicoVO.getCctCodigo())) {
				continue;
			}

			// Passo 4
			retornoObjetoCustoComProducaoParaRateio = this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().buscarObjetosCustoComProducaoParaRateio(sigProcessamentoCusto.getSeq(), contratoServicoVO.getCctCodigo());

			if(ProcessamentoCustoUtils.verificarListaNaoVazia(retornoObjetoCustoComProducaoParaRateio)){
				
				for(ObjetoCustoProducaoRateioVO  objetoCustoVO : retornoObjetoCustoComProducaoParaRateio) {
	
					// Passo 5
					SigCalculoRateioServico calculoRateioServico = new SigCalculoRateioServico();
					calculoRateioServico.setCriadoEm(new Date());
					calculoRateioServico.setRapServidores(rapServidores);
					SigCalculoObjetoCusto sigCalculoObjetoCusto = this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().obterPorChavePrimaria(objetoCustoVO.getCbjSeq());
					calculoRateioServico.setSigCalculoObjetoCustos(sigCalculoObjetoCusto);
					calculoRateioServico.setScoAfContrato(afContrato);
					calculoRateioServico.setAutorizacaoForn(autorizacaoForn);
					calculoRateioServico.setServico(servico);
					calculoRateioServico.setGrupoServico(calculoRateioServico.getServico().getGrupoServico());
					calculoRateioServico.setQtde(this.calcularQuantidade(objetoCustoVO.getPesoOc(),objetoCustoVO.getSomaPesos()));
					calculoRateioServico.setVlrItemContrato(this.calcularValorItemContrato(contratoServicoVO.getVlrItemContrato(),objetoCustoVO.getPesoOc(),objetoCustoVO.getSomaPesos()));
					calculoRateioServico.setPeso(objetoCustoVO.getPesoOc());
					this.getProcessamentoCustoUtils().getSigCalculoRateioServicoDAO().persistir(calculoRateioServico);
	
					// Passo 6
					sigCalculoObjetoCusto.setVlrRateioServico(sigCalculoObjetoCusto.getVlrRateioServico().add(calculoRateioServico.getVlrItemContrato()));
					this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().atualizar(sigCalculoObjetoCusto);
					
					
					// Passo 8
					SigMvtoContaMensal sigMvtoContaMensal = new SigMvtoContaMensal();
					sigMvtoContaMensal.setSigProcessamentoCustos(sigProcessamentoCusto);
					sigMvtoContaMensal.setCriadoEm(new Date());
					sigMvtoContaMensal.setRapServidores(rapServidores);
					sigMvtoContaMensal.setTipoMvto(DominioTipoMovimentoConta.VAR);
					sigMvtoContaMensal.setTipoValor(DominioTipoValorConta.DS);
					sigMvtoContaMensal.setFccCentroCustos(this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(contratoServicoVO.getCctCodigo()));
					sigMvtoContaMensal.setScoAfContrato(afContrato);
					sigMvtoContaMensal.setAutorizacaoForn(autorizacaoForn);
					sigMvtoContaMensal.setSolicitacaoServico(this.getProcessamentoCustoUtils().getComprasFacade().obterDescricaoSolicitacaoServicoPeloId(contratoServicoVO.getSlsNumero()));
					sigMvtoContaMensal.setServico(servico);
					sigMvtoContaMensal.setQtde(Long.valueOf(1));
					sigMvtoContaMensal.setValor(contratoServicoVO.getVlrItemContrato().negate());
					this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().persistir(sigMvtoContaMensal);

					// Passo 9
					this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,sigProcessamentoPassos,DominioEtapaProcessamento.S,"MENSAGEM_SUCESSO_RATEIO_SERVICO",contratoServicoVO.getSlsNumero(),identifcadorContrato);
	
				}
			} else{
					// [FE01] - Identifca que não possui centro de custo (já que scrollable)
				// Determina que esse centro de custo não possui
				centrosCustoSemObjetoCustoComProducao.add(contratoServicoVO.getCctCodigo());
				// Grava o log e continua para o próximo registro
				this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto,rapServidores,sigProcessamentoPassos,DominioEtapaProcessamento.S,"MENSAGEM_CENTRO_CUSTO_NAO_POSSUI_OBJETO_CUSTO",contratoServicoVO.getCctCodigo());
			}
		}// Passo 10: repete os passos 5, 6, 7, 8 e 9.

		// ATENÇÃO:
		// "Passo 11: volta ao passo 4 para buscar os objetos de custo do novo/próximo centro de custo".
		// O scrollable não "volta atrás", então a consulta tem que ser refeita
		// novamente
		retornoCustosContrato.close();
		this.commitProcessamentoCusto();

		// Passo 12
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,sigProcessamentoPassos, DominioEtapaProcessamento.S,"MENSAGEM_SUCESSO_CALCULO_RATEIO_SERVICOS");
	}

	/**
	 * Calcula valor do item de contrato através da seguinte fórmula:
	 * VLR_ITEM_CONTRATO = VLR_ITEM_CONTRATO_TOTAL / PESO_ACUMULADO * PESO_OC
	 * 
	 * @author rogeriovieira
	 * @param vlrItemServicoTotal
	 *            valor do item de contrato
	 * @param pesoOc
	 *            peso do objeto de custo
	 * @param somaPesos
	 *            soma dos pesos
	 * @return resultado do cálculo
	 */
	private BigDecimal calcularValorItemContrato(BigDecimal vlrItemServicoTotal, BigDecimal pesoOc,BigDecimal somaPesos) {
		return ProcessamentoCustoUtils.dividir(vlrItemServicoTotal, somaPesos).multiply(pesoOc);
	}

	/**
	 * Calcula a quantidade de acordo com a seguinte fórmula: QTDE = PESO_OC /
	 * PESO_ACUMULADO
	 * 
	 * @author rogeriovieira
	 * @param pesoOc
	 * @param somaPesos
	 * @return
	 */
	private Double calcularQuantidade(BigDecimal pesoOc, BigDecimal somaPesos) {
		return ProcessamentoCustoUtils.dividir(pesoOc, somaPesos).doubleValue();
	}
}
