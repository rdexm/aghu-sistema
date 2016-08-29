package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoControleVidaUtil;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoConta;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoValorConta;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigControleVidaUtil;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * ETAPA 2: executa o débito das parcelas de materiais com vida útil por tempo.
 * Estória de Usuário #16834 - Processamento mensal - entre as páginas 17 e 18
 * 
 * @author rogeriovieira
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalContabilizacaoDebitoParcelaMaterialRN.class)
public class ProcessamentoCustosMensalContabilizacaoDebitoParcelaMaterialRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = -5811664870339719782L;

	@Override
	public String getTitulo() {
		return "Executa o debito de parcelas de materiais com vida util por tempo.";
	}

	@Override
	public String getNome() {
		return "ProcessamentoCustoMensalContabilizacaoDebitoParcelaMaterialON - processamentoInsumosEtapa2";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 2;
	}

	@Override
	protected void executarPassosInternos(
			SigProcessamentoCusto sigProcessamentoCusto,
			RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto)
			throws ApplicationBusinessException {
		this.processamentoInsumosEtapa2(sigProcessamentoCusto, rapServidores,
				sigProcessamentoPassos);
	}

	/**
	 * Executa todos os passos do processamento da etapa 2
	 * 
	 * @author rogeriovieira
	 * @param sigProcessamentoCusto
	 *            Processamento Atual.
	 * @param rapServidores
	 *            Servidor Logado.
	 * @param sigProcessamentoPassos
	 *            Passo Atual.
	 * @throws ApplicationBusinessException
	 *             Exceção lançada se alguma coisa aconteceu na hora do commit
	 *             do processamento.
	 */
	private void processamentoInsumosEtapa2(
			SigProcessamentoCusto sigProcessamentoCusto,
			RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos)
			throws ApplicationBusinessException {

		// Passo 1 - grava inicio da contabilização de parcelas de materiais no
		// log
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_CONTABILIZACAO_PARCELAS_MATERIAIS");
		
		// Passo 2 - faz a busca de materiais com controle de vida util por
		// tempo e que ainda estão ativos
		List<SigControleVidaUtil> listaControleVidaUtil = this
				.getProcessamentoCustoUtils()
				.getSigControleVidaUtilDAO()
				.buscarDebitosControleVidaUtilParaProcessamentoMensal(
						DominioSituacao.A, DominioTipoControleVidaUtil.T);

		// Passo 3 - grava execução da busca do materiais no log.
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_SUCESSO_BUSCA_PARCELAS_MATERIAIS");

		// Passo 4 - para cada linha recuperada na consulta, insere os dados de
		// consumo do mês na tabela SIG_MVTO_CONTA_MENSAIS
		for (SigControleVidaUtil sigVidaUtil : listaControleVidaUtil) {

			SigMvtoContaMensal mvo = new SigMvtoContaMensal();

			mvo.setSigProcessamentoCustos(sigProcessamentoCusto);
			mvo.setCriadoEm(new Date());
			mvo.setRapServidores(rapServidores);
			mvo.setTipoMvto(DominioTipoMovimentoConta.SVU);
			mvo.setTipoValor(DominioTipoValorConta.DI);
			mvo.setFccCentroCustos(sigVidaUtil.getFccCentroCustos());
			mvo.setScoMaterial(sigVidaUtil.getScoMaterial());
			mvo.setQtde(0L);
			mvo.setValor(sigVidaUtil.getValorMes());

			this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO()
					.persistir(mvo);

			// Passo 5 - Verifica se a data de fim do controle do material é
			// igual ao mês e ano da competência do processamento.
			if (this.compararDatasFimCompetenciaComCompetenciaProcessamento(
					sigVidaUtil.getDataFim(),
					sigProcessamentoCusto.getCompetencia())) {
				sigVidaUtil.setIndSituacao(DominioSituacao.I);
				this.getProcessamentoCustoUtils()
						.getSigControleVidaUtilDAO().atualizar(sigVidaUtil);
			}
		}// Passo 6

		this.commitProcessamentoCusto();
		// Passo 7 - Grava execução da busca do consumo de insumos no log.
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_SUCESSO_GRAVAR_PARCELAS_MATERIAIS");
	}

	/**
	 * Corresponde ao passo 5 desta etapa, no qual é verificada se a data de fim
	 * da vida util do registro é no mesmo mês do processamento executado
	 * 
	 * @author rogeriovieira
	 * @param dataFimVidaUtil
	 *            data de fim da vida util
	 * @param competenciaProcessamento
	 *            competencia do processamento
	 * @return verdadeiro se o mês do processamento for o mesmo do fim da vida
	 *         útil
	 */
	private boolean compararDatasFimCompetenciaComCompetenciaProcessamento(
			Date dataFimVidaUtil, Date competenciaProcessamento) {
		if (dataFimVidaUtil != null) {

			Calendar dataVida = Calendar.getInstance();
			dataVida.setTime(dataFimVidaUtil);

			Calendar dataProcessamento = Calendar.getInstance();
			dataProcessamento.setTime(competenciaProcessamento);

			if (dataProcessamento.get(Calendar.MONTH) == dataVida
					.get(Calendar.MONTH)
					&& dataProcessamento.get(Calendar.YEAR) == dataVida
							.get(Calendar.YEAR)) {
				return true;
			}
		}
		return false;
	}
}
