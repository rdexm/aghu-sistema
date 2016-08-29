package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * ETAPA 22: Processar custos indiretos nos objetos de custo Estória de Usuário
 * #23093
 * 
 * @author rogeriovieira
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalIndiretoObjetoCustoRN.class)
public class ProcessamentoCustosMensalIndiretoObjetoCustoRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = 926721298285886272L;

	@Override
	public String getTitulo() {
		return "Processa e repassa o custo do insumos dos objetos de custo de apoio.";
	}

	@Override
	public String getNome() {
		return "processaERepassaCustoIndiretoApoio - processamentoCustoIndireto22";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 22;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos, DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		this.processarEtapa1(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
		this.processarEtapa2(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
	}

	/**
	 * Executa todos os passos do processamento deste etapa
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
	private void processarEtapa1(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {
		// Faz a chamada para o fluxo equivalente a etapa 4 do processamento de clientes,
		// mas informa que não são os clientes que serão processados e sim os objetos de custo
		this.processamentoRateioIndiretos(sigProcessamentoCusto, rapServidores,sigProcessamentoPassos, false, 0);
	}

	/**
	 * Totalizar os custos indiretos nos objetos de custo assistenciais.
	 * 
	 * @author rmalvezzi
	 * @throws ApplicationBusinessException
	 */
	private void processarEtapa2(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {

		// Passo 1
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores,
				sigProcessamentoPassos, DominioEtapaProcessamento.N, "MENSAGEM_INICIO_ETAPA2_INDIRETOS_OC");

		// Passo 2
		List<SigCalculoObjetoCusto> calculoObjetoCustos = this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO()
				.buscarCalculosObjetoCustoPorCompetencia(sigProcessamentoCusto);

		// [FE01]
		if (!ProcessamentoCustoUtils.verificarListaNaoVazia(calculoObjetoCustos)) {
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores,
					sigProcessamentoPassos, DominioEtapaProcessamento.N, "MENSAGEM_ETAPA2_FE01",
					sigProcessamentoCusto.getCompetenciaMesAno());
			return;
		}

		// Passo 3
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,
				sigProcessamentoPassos, DominioEtapaProcessamento.N, "MENSAGEM_ETAPA2_OC_SUCESSO");

		for (SigCalculoObjetoCusto sigCalculoObjetoCusto : calculoObjetoCustos) {
			BigDecimal vlrRateioIndiretos = sigCalculoObjetoCusto.getVlrIndInsumos().add(
					sigCalculoObjetoCusto.getVlrIndPessoas().add(
							sigCalculoObjetoCusto.getVlrIndEquipamentos().add(sigCalculoObjetoCusto.getVlrIndServicos())));
			sigCalculoObjetoCusto.setVlrRateioIndiretos(vlrRateioIndiretos);
			this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().atualizar(sigCalculoObjetoCusto);
		}

		this.commitProcessamentoCusto();

		// Passo 5
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores,
				sigProcessamentoPassos, DominioEtapaProcessamento.N, "MENSAGEM_ETAPA2_OC_TERMINO");

	}

	
}
