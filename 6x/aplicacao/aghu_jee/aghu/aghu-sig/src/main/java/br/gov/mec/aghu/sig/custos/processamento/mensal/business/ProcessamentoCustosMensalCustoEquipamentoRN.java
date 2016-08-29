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
import br.gov.mec.aghu.model.SigAtividadeEquipamentos;
import br.gov.mec.aghu.model.SigCalculoAtividadeEquipamento;
import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.business.SigNoEnumOperationException;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.EquipamentoAtividadeObjetoCustoVO;

/**
 * Classe responsável pela processar os valores de depreciação mensal dos equipamentos dos centros de custos têm seu custo alocado nas atividades 
 * e, por conseqüência, nos objetos de custo (produtos/serviços) da área.
 * 
 * #14920 e #24807
 * 
 * @author rmalvezzi
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalCustoEquipamentoRN.class)
public class ProcessamentoCustosMensalCustoEquipamentoRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = -4866342497225330580L;

	@Override
	public String getTitulo() {
		return "Procesar o custo dos equipamentos das atividades.";
	}

	@Override
	public String getNome() {
		return "processamentoCustoEquipamentoAtividadeRN - processamentoEquipamentos";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return ProcessamentoCustoUtils.definirPassoUtilizado(tipoObjetoCusto, 9, 15);
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		this.processamentoEquipamentos(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, tipoObjetoCusto);
	}

	/**
	 * Fluxo principal do Cálculo de distribuição de depreciação de equipamentos associados a atividades
	 * 
	 * @author rmalvezzi
	 * @param sigProcessamentoCusto					Processamento Atual.
	 * @param rapServidores							Servidor Logado.
	 * @param sigProcessamentoPassos				Passo Atual.
	 * @param tipoObjetoCusto						Se a etapa de processamento é Assistencial ou Apoio.
	 * @throws ApplicationBusinessException		Exceção lançada se alguma coisa aconteceu na hora do commit do processamento.
	 */
	public void processamentoEquipamentos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos, DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {

		//Passo 1
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.E, "MENSAGEM_INICIO_PROCESSAMENTO_EQUIPAMENTOS");

		//Passo 2
		ScrollableResults retorno = null;
		String chave = "";
		String chaveFinal = "";

		switch (tipoObjetoCusto) {
		case AS:
			retorno = this.getProcessamentoCustoUtils().getSigAtividadeEquipamentosDAO()
					.buscaEquipamentosAlocadosAtividadesAssistencial(sigProcessamentoCusto.getSeq());
			chave = "MENSAGEM_SUCESSO_BUSCA_EQUIPAMENTOS_ALOCADOS_ASSISTENCIAL";
			chaveFinal = "MENSAGEM_SUCESSO_CALCULO_DEPRECIACAO_ASSISTENCIAL";
			break;
		case AP:
			retorno = this.getProcessamentoCustoUtils().getSigAtividadeEquipamentosDAO()
					.buscaEquipamentosAlocadosAtividadesApoio(sigProcessamentoCusto.getSeq());
			chave = "MENSAGEM_SUCESSO_BUSCA_EQUIPAMENTOS_ALOCADOS_APOIO";
			chaveFinal = "MENSAGEM_SUCESSO_CALCULO_DEPRECIACAO_APOIO";
			break;
		default:
			throw new SigNoEnumOperationException();
		}

		//Passo 3
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.E, chave);

		BigDecimal acumulaPeso = BigDecimal.ZERO;
		String codPatrimonioCorrrente = null;
		Integer codCentroCustoCorrrente = null;
		List<SigCalculoAtividadeEquipamento> listaCalculoAtividadeEquipamentos = new ArrayList<SigCalculoAtividadeEquipamento>();

		//Passo 11
		if (retorno != null) {
			while (retorno.next()) {

				EquipamentoAtividadeObjetoCustoVO vo = new EquipamentoAtividadeObjetoCustoVO((Object[]) retorno.get());

				if (codPatrimonioCorrrente == null) {
					codPatrimonioCorrrente = vo.getCodPatrimonio();
					codCentroCustoCorrrente = vo.getCctCodigo();

					//Passo 6	
				} else if (!codPatrimonioCorrrente.equals(vo.getCodPatrimonio())) {

					this.executaEtapas7e8e9e10eRN01(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, codPatrimonioCorrrente,
							listaCalculoAtividadeEquipamentos, acumulaPeso, codPatrimonioCorrrente, codCentroCustoCorrrente);

					acumulaPeso = BigDecimal.ZERO;
					codPatrimonioCorrrente = vo.getCodPatrimonio();
					codCentroCustoCorrrente = vo.getCctCodigo();
					listaCalculoAtividadeEquipamentos = new ArrayList<SigCalculoAtividadeEquipamento>();
				}

				//Passo 4
				SigCalculoAtividadeEquipamento sigCalculoAtividadeEquipamento = new SigCalculoAtividadeEquipamento();
				sigCalculoAtividadeEquipamento.setCriadoEm(new Date());
				sigCalculoAtividadeEquipamento.setRapServidores(rapServidores);
				sigCalculoAtividadeEquipamento.setSigCalculoComponente(this.getProcessamentoCustoUtils().getSigCalculoComponenteDAO()
						.obterPorChavePrimaria(vo.getCmtSeq()));
				sigCalculoAtividadeEquipamento.setSigAtividades(this.getProcessamentoCustoUtils().getSigAtividadesDAO()
						.obterPorChavePrimaria(vo.getTvdSeq()));

				SigAtividadeEquipamentos atividadeEquipamentos = this.getProcessamentoCustoUtils().getSigAtividadeEquipamentosDAO()
						.obterPorChavePrimaria(vo.getAveSeq());
				sigCalculoAtividadeEquipamento.setSigAtividadeEquipamentos(atividadeEquipamentos);
				sigCalculoAtividadeEquipamento.setSigDirecionadores(atividadeEquipamentos.getSigDirecionadores());

				sigCalculoAtividadeEquipamento.setCodPatrimonio(vo.getCodPatrimonio());
				sigCalculoAtividadeEquipamento.setPeso(vo.getPesoOc());
				sigCalculoAtividadeEquipamento.setVlrDepreciacao(BigDecimal.ZERO);

				this.getProcessamentoCustoUtils().getSigCalculoAtividadeEquipamentoDAO().persistir(sigCalculoAtividadeEquipamento);

				//Passo 5
				acumulaPeso = acumulaPeso.add(vo.getPesoOc());
				listaCalculoAtividadeEquipamentos.add(sigCalculoAtividadeEquipamento);
			}

			if (ProcessamentoCustoUtils.verificarListaNaoVazia(listaCalculoAtividadeEquipamentos)) {
				this.executaEtapas7e8e9e10eRN01(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, codPatrimonioCorrrente,
						listaCalculoAtividadeEquipamentos, acumulaPeso, codPatrimonioCorrrente, codCentroCustoCorrrente);
			}

			retorno.close();
			this.commitProcessamentoCusto();
		}
		//Passo 12
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.E, chaveFinal);
	}

	/**
	 * Calculo do valor da depreciação do equipamento na atividade.
	 * 
	 * @author rmalvezzi
	 * @param sigProcessamentoCusto			Processamento Atual.
	 * @param rapServidores					Servido logado.
	 * @param sigProcessamentoPassos		Passo atual.
	 * @param codCentroCusto				Codigo do Centro de Custo.
	 * @param codPatrimonio					Codigo do Patrimonio. 
	 * @return								Valor da depreciação do equipamento na atividade
	 */
	private BigDecimal calculaValorDepreciacaoEquipamentoAtividadeRN01(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos, Integer codCentroCusto, String codPatrimonio) {

		Object[] buscaSomatorioConsumoInsumoMaterialCentroCusto = this
				.getProcessamentoCustoUtils()
				.getSigMvtoContaMensalDAO()
				.buscarSomatorioConsumoInsumoMaterialCentroCusto(sigProcessamentoCusto, null, null, codPatrimonio,
						this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(codCentroCusto));

		BigDecimal vlrDeprecEquipamento = (BigDecimal) buscaSomatorioConsumoInsumoMaterialCentroCusto[1];

		//[FE01]
		if (vlrDeprecEquipamento == null) {
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,
					sigProcessamentoPassos, DominioEtapaProcessamento.E, "MENSAGEM_EQUIPAMENTO_SEM_VALOR_DEPRECIACAO_MENSAL", codPatrimonio,
					sigProcessamentoCusto.getCompetenciaMesAno());
			return null;
		}

		return vlrDeprecEquipamento;
	}

	/**
	 * 
	 * @author rmalvezzi
	 * @param sigProcessamentoCusto					Processamento Atual.
	 * @param rapServidores							Servido logado.
	 * @param sigProcessamentoPassos				Passo atual.
	 * @param codPatrimonio							Codigo do Patrimonio. 
	 * @param listaCalculoAtividadeEquipamentos		Lista dos calculos de equipamentos inseridos.
	 * @param acumulaPeso							Peso Acumulado.
	 * @param codPatrimonioCorrrente				Codigo do Patrimonio.
	 * @param codCentroCustoCorrrente				Codigo do Centro de Custo.
	 */
	private void executaEtapas7e8e9e10eRN01(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos, String codPatrimonio, List<SigCalculoAtividadeEquipamento> listaCalculoAtividadeEquipamentos,
			BigDecimal acumulaPeso, String codPatrimonioCorrrente, Integer codCentroCustoCorrrente) {

		//[RN01]
		BigDecimal vlrDeprecEquipamento = this.calculaValorDepreciacaoEquipamentoAtividadeRN01(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				codCentroCustoCorrrente, codPatrimonioCorrrente);

		if (vlrDeprecEquipamento != null) {

			for (SigCalculoAtividadeEquipamento sigCalculoAtividadeEquipamentoItem : listaCalculoAtividadeEquipamentos) {

				//[RN01]
				BigDecimal vlrDeprec = ProcessamentoCustoUtils.dividir(vlrDeprecEquipamento, acumulaPeso).multiply(sigCalculoAtividadeEquipamentoItem.getPeso());

				sigCalculoAtividadeEquipamentoItem.setVlrDepreciacao(vlrDeprec);
				this.getProcessamentoCustoUtils().getSigCalculoAtividadeEquipamentoDAO().atualizar(sigCalculoAtividadeEquipamentoItem);

				//Passo 7
				SigCalculoComponente sigCalculoComponente = sigCalculoAtividadeEquipamentoItem.getSigCalculoComponente();
				sigCalculoComponente.setVlrEquipamento(sigCalculoComponente.getVlrEquipamento().add(vlrDeprec));
				this.getProcessamentoCustoUtils().getSigCalculoComponenteDAO().atualizar(sigCalculoComponente);

				//Passo 8
				SigCalculoObjetoCusto sigCalculoObjetoCusto = sigCalculoComponente.getSigCalculoObjetoCustosByCbjSeq();
				sigCalculoObjetoCusto.setVlrAtvEquipamento(sigCalculoObjetoCusto.getVlrAtvEquipamento().add(vlrDeprec));
				this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().atualizar(sigCalculoObjetoCusto);

			}

			//Passo 9
			SigMvtoContaMensal sigMvtoContaMensal = new SigMvtoContaMensal();
			sigMvtoContaMensal.setSigProcessamentoCustos(sigProcessamentoCusto);
			sigMvtoContaMensal.setCriadoEm(new Date());
			sigMvtoContaMensal.setRapServidores(rapServidores);
			sigMvtoContaMensal.setTipoMvto(DominioTipoMovimentoConta.VAA);
			sigMvtoContaMensal.setTipoValor(DominioTipoValorConta.DE);
			sigMvtoContaMensal.setFccCentroCustos(this.getProcessamentoCustoUtils().getCentroCustoFacade()
					.obterCentroCustoPorChavePrimaria(codCentroCustoCorrrente));
			sigMvtoContaMensal.setCodPatrimonio(codPatrimonioCorrrente);
			sigMvtoContaMensal.setQtde(Long.valueOf(1));
			sigMvtoContaMensal.setValor(vlrDeprecEquipamento.negate());

			this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().persistir(sigMvtoContaMensal);

			//Passo 10
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores,
					sigProcessamentoPassos, DominioEtapaProcessamento.E, "MENSAGEM_SUCESSO_CALCULO_DEPRECIACAO_EQUIPAMENTO", codPatrimonioCorrrente);
		}

	}
}
