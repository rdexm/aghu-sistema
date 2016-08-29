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
 * Essa estória tem por objetivo descrever a forma como os objetos de custos que
 * tiveram produção no mês de referência do processamento terão seu custo
 * sumarizado, em outras palavras, descrever como vamos obter o custo total de
 * cada objeto de custo. Esta deve ser a última etapa a ser executada no
 * processamento mensal do módulo de custos. #30653
 * 
 * @author rmalvezzi
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalSumarizarCustoMedioRN.class)
public class ProcessamentoCustosMensalSumarizarCustoMedioRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = -5811664870339719787L;

	@Override
	public String getTitulo() {
		return "Sumarizar o valor dos objetos de custo com produção no mês de processamento.";
	}

	@Override
	public String getNome() {
		return "processamentoMensalSumarizarCustoMedioON - processamentoCustosMediosEtapa23";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 23;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		this.processamentoEtapa23(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
	}

	/**
	 * Fluxo Principal dessa etapa - Sumarizar o valor dos objetos de custo com produção no mês de processamento
	 *  
	 * @author rmalvezzi
	 * @param sigProcessamentoCusto					Processamento Atual.
	 * @param rapServidores							Servidor Logado.
	 * @param sigProcessamentoPassos				Passo Atual.
	 * @throws ApplicationBusinessException		Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 */
	public void processamentoEtapa23(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos)
			throws ApplicationBusinessException {

		//Passo 1
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.G, "MESSAGE_ETAPA_SUMARIZAR_CUSTO_MEDIO");

		//Passo 2
		List<SigCalculoObjetoCusto> buscarCalculosObjetoCustoPorCompetencia = this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO()
				.buscarCalculosObjetoCustoPorCompetencia(sigProcessamentoCusto);

		//Passo 3
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.G, "MESSAGE_ETAPA_SUMARIZAR_SUCESSO", sigProcessamentoCusto.getCompetenciaMesAno());

		if (ProcessamentoCustoUtils.verificarListaNaoVazia(buscarCalculosObjetoCustoPorCompetencia)) {

			//Passo 4
			for (SigCalculoObjetoCusto sigCalculoObjetoCusto : buscarCalculosObjetoCustoPorCompetencia) {

				//[RN01]
				BigDecimal totalAtvDir = sigCalculoObjetoCusto.getVlrAtvInsumos()
						.add(sigCalculoObjetoCusto.getVlrAtvPessoal())
						.add(sigCalculoObjetoCusto.getVlrAtvEquipamento())
						.add(sigCalculoObjetoCusto.getVlrAtvServicos());

				BigDecimal totalRateio = sigCalculoObjetoCusto.getVlrRateioInsumos()
						.add(sigCalculoObjetoCusto.getVlrRateioPessoas())
						.add(sigCalculoObjetoCusto.getVlrRateioEquipamentos())
						.add(sigCalculoObjetoCusto.getVlrRateioServico());

				BigDecimal totalAtvInd = sigCalculoObjetoCusto.getVlrIndInsumos()
						.add(sigCalculoObjetoCusto.getVlrIndPessoas())
						.add(sigCalculoObjetoCusto.getVlrIndEquipamentos())
						.add(sigCalculoObjetoCusto.getVlrIndServicos());

				BigDecimal totalCalculado = totalAtvDir.add(totalRateio).add(totalAtvInd);				

				sigCalculoObjetoCusto.setVlrObjetoCusto(totalCalculado);
				
				this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().atualizar(sigCalculoObjetoCusto);
			}

			this.commitProcessamentoCusto();

			//Passo 5
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
					DominioEtapaProcessamento.G, "MESSAGE_ETAPA_SUMARIZAR_CM_FINAL");
		} else {
			//[FE01]
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
					DominioEtapaProcessamento.G, "MESSAGE_ETAPA_SUMARIZAR_FE01");
		}
	}
}
