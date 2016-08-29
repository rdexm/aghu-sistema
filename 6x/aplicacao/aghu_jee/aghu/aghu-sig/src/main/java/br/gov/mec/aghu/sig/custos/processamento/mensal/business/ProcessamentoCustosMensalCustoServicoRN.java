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
import br.gov.mec.aghu.model.SigAtividadeServicos;
import br.gov.mec.aghu.model.SigCalculoAtividadeServico;
import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.ServicoAtividadeObjetoCustoVO;
import br.gov.mec.aghu.view.VSigAfsContratosServicos;

/**
 * #14922 - 24808 - Processar o custo de serviço das atividades Apoio/Assistencial. 
 * 
 * Defini como os custos de serviços, referentes a contratos e autorizações de fornecimento (AF) de serviços dos centros de custos, 
 * têm seu custo alocado nas atividades e, por conseqüência, nos objetos de custo (produtos/serviços) da área Apoio e Assitencial.
 * 
 * @author jgugel
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalCustoServicoRN.class)
public class ProcessamentoCustosMensalCustoServicoRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = -5421975053724294988L;

	@Override
	public String getTitulo() {
		return "Processar o custo dos serviços das atividades.";
	}

	@Override
	public String getNome() {
		return "ProcessamentoMensalCustoServicoON - processamentoServico";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return ProcessamentoCustoUtils.definirPassoUtilizado(tipoObjetoCusto, 10, 16);
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		this.processarServico(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, tipoObjetoCusto);
	}

	public void processarServico(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {

		// Passo 01
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.S, "MENSAGEM_INICIO_PROCESSAMENTO_SERVICOS");

		//Passo 02 
		ScrollableResults retorno = null;
		String mensagem = "";

		switch (tipoObjetoCusto) {
			case AS:
				retorno = this.getProcessamentoCustoUtils().getSigAtividadeServicosDAO().buscaItensContratoAlocadosAtividades(sigProcessamentoCusto.getSeq());
				mensagem = "MENSAGEM_SUCESSO_BUSCA_SERVICOS_ALOCADOS";
				break;
			case AP:
				retorno = this.getProcessamentoCustoUtils().getSigAtividadeServicosDAO()
						.buscaItensContratoAlocadosAtividadesApoio(sigProcessamentoCusto.getSeq());
				mensagem = "MENSAGEM_SUCESSO_BUSCA_SERVICOS_ALOCADOS_APOIO";
				break;
			default:
				throw new UnsupportedOperationException("No suport for Enum type.");
		}

		//Passo 03
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.S, mensagem);

		//Variaveis utilizadas para controle interno.
		BigDecimal acumulaPeso = BigDecimal.ZERO;
		Integer cctCorrente = null;
		String seqCorrrente = null;
		List<SigCalculoAtividadeServico> listCalculoAtividades = new ArrayList<SigCalculoAtividadeServico>();
		List<ServicoAtividadeObjetoCustoVO> listaVOsCorrentes = new ArrayList<ServicoAtividadeObjetoCustoVO>();

		while (retorno.next()) {
			ServicoAtividadeObjetoCustoVO servicoAtividadeObjeto = new ServicoAtividadeObjetoCustoVO((Object[]) retorno.get());

			if (seqCorrrente == null || cctCorrente == null) {
				seqCorrrente = this.montaSeqItemComCentroCusto(servicoAtividadeObjeto);
				cctCorrente = servicoAtividadeObjeto.getCctCodigo();
			}

			//Passo 04
			SigCalculoAtividadeServico calculoAtividadeServico = this.cadastrarCalculoAtividadeServico(rapServidores, servicoAtividadeObjeto);

			//Passo 05
			if (this.verificaMudouItemOUCentroCusto(seqCorrrente, servicoAtividadeObjeto)) {

				//chamada demais passos
				this.efetuaCadastrosSobreAMudancaDeItem(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, acumulaPeso, listCalculoAtividades,
						cctCorrente);

				// Setters variaveis auxiliares
				acumulaPeso = BigDecimal.ZERO;
				seqCorrrente = this.montaSeqItemComCentroCusto(servicoAtividadeObjeto);
				listaVOsCorrentes = new ArrayList<ServicoAtividadeObjetoCustoVO>();
				listCalculoAtividades = new ArrayList<SigCalculoAtividadeServico>();
				cctCorrente = servicoAtividadeObjeto.getCctCodigo();
			}

			// Passo 05 - Acumulo do peso enquanto não muda.
			acumulaPeso = acumulaPeso.add(servicoAtividadeObjeto.getPesoOc());
			listaVOsCorrentes.add(servicoAtividadeObjeto);
			listCalculoAtividades.add(calculoAtividadeServico);

		}

		//Esse ultimo cadastro pega o ultimo item do laço. Obrigatório o mesmo estar presente.
		if (listCalculoAtividades != null && listCalculoAtividades.size() > 0) {
			this.efetuaCadastrosSobreAMudancaDeItem(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, acumulaPeso, listCalculoAtividades,
					cctCorrente);
		}

		retorno.close();
		this.commitProcessamentoCusto();

		// Passo 12
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.S, "MENSAGEM_SUCESSO_CALCULO_CUSTO_CONTRATO");
	}

	/**
	 * 
	 * Efetua toda a lógica descrita no documento de analise apos a mudança de um item de contrato ou uma AF ou um centro de custo são alterados.
	 * 
	 * @param sigProcessamentoCusto
	 * @param rapServidores
	 * @param sigProcessamentoPassos
	 * @param pesoAcumulado somatorio dos pesos até ser efetuado a troca. 
	 * @param listCalculoAtividades atividades que possuem a mesma af ou item de contrato
	 * @param cctCodigo centro custo das atividades acima
	 * @throws ApplicationBusinessException
	 * @author jgugel
	 */
	private void efetuaCadastrosSobreAMudancaDeItem(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos, BigDecimal pesoAcumulado, List<SigCalculoAtividadeServico> listCalculoAtividades, Integer cctCodigo)
			throws ApplicationBusinessException {

		if (listCalculoAtividades != null && listCalculoAtividades.size() > 0) {

			SigCalculoAtividadeServico calculoAtividade01 = listCalculoAtividades.get(0);
			BigDecimal vlrItemContrato = this.verificaOcorreuCustoMensalServico(calculoAtividade01, sigProcessamentoCusto, cctCodigo);

			if (vlrItemContrato != null && vlrItemContrato.doubleValue() > 0D) {

				for (SigCalculoAtividadeServico calculoAtividade : listCalculoAtividades) {
					//Executa calculo da RN01
					Double vlrCalculado = (vlrItemContrato.doubleValue() / pesoAcumulado.doubleValue()) * calculoAtividade.getPeso().doubleValue();
					calculoAtividade.setVlrItemContrato(BigDecimal.valueOf(vlrCalculado));
					this.getProcessamentoCustoUtils().getSigCalculoAtividadeServicoDAO().atualizar(calculoAtividade);

					// Passo 07
					SigCalculoComponente sigCalculoComponente = calculoAtividade.getSigCalculoComponentes();
					sigCalculoComponente.setVlrServicos(sigCalculoComponente.getVlrServicos().add(calculoAtividade.getVlrItemContrato()));
					this.getProcessamentoCustoUtils().getSigCalculoComponenteDAO().atualizar(sigCalculoComponente);

					// Passo 08
					SigCalculoObjetoCusto sigCalculoObjetoCusto = sigCalculoComponente.getSigCalculoObjetoCustosByCbjSeq();
					sigCalculoObjetoCusto.setVlrAtvServicos(sigCalculoObjetoCusto.getVlrAtvServicos().add(calculoAtividade.getVlrItemContrato()));
					this.getProcessamentoCustoUtils().getSigCalculoObjetoCustoDAO().atualizar(sigCalculoObjetoCusto);

				}

				// Passo 09
				SigMvtoContaMensal sigMvtoContaMensal = new SigMvtoContaMensal();
				sigMvtoContaMensal.setSigProcessamentoCustos(sigProcessamentoCusto);
				sigMvtoContaMensal.setCriadoEm(new Date());
				sigMvtoContaMensal.setRapServidores(rapServidores);
				sigMvtoContaMensal.setTipoMvto(DominioTipoMovimentoConta.VAA);
				sigMvtoContaMensal.setTipoValor(DominioTipoValorConta.DS);
				sigMvtoContaMensal.setFccCentroCustos(this.getProcessamentoCustoUtils().getCentroCustoFacade()
						.obterCentroCustoPorChavePrimaria(cctCodigo));
				sigMvtoContaMensal.setScoAfContrato(calculoAtividade01.getScoAfContrato());
				sigMvtoContaMensal.setAutorizacaoForn(calculoAtividade01.getAutorizacaoForn());
				sigMvtoContaMensal.setServico(calculoAtividade01.getServico());
				sigMvtoContaMensal.setQtde(Long.valueOf(1));
				sigMvtoContaMensal.setValor(vlrItemContrato.negate());
				this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO().persistir(sigMvtoContaMensal);

				// Passo 10
				this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.S, "MENSAGEM_SUCESSO_CALCULO_ITEM_CONTRATO_SERVICO",
						calculoAtividade01.getSigAtividades().getSeq());

			} else {
				if (calculoAtividade01.getScoAfContrato() != null) {
					// log FE01 da RN01
					this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.S, "MENSAGEM_FE01_CONTRATO_SEM_CUSTO_MENSAL",
							calculoAtividade01.getScoAfContrato().getSeq(), sigProcessamentoCusto.getCompetenciaMesAno());
				} else {
					// log FE02 da RN01
					this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.S, "MENSAGEM_FE02_CONTRATO_SEM_CUSTO_MENSAL",
							calculoAtividade01.getAutorizacaoForn().getNumero(), calculoAtividade01.getServico().getCodigo(), sigProcessamentoCusto.getCompetenciaMesAno());
				}
			}
		}
	}

	/**
	 * Executa a verificação se ocorreu ou não se af ou item de contrato teve custo no mês.
	 * @param calculoAtividade 
	 * @param sigProcessamentoCusto
	 * @param cctCodigo
	 * @return null ou 0 se não teve, ou o custo caso ocorreu no mês do processamento.
	 * @author jgugel
	 */
	private BigDecimal verificaOcorreuCustoMensalServico(SigCalculoAtividadeServico calculoAtividade, SigProcessamentoCusto sigProcessamentoCusto,
			Integer cctCodigo) {
		BigDecimal vlrItemContratoServico = null;

		if (calculoAtividade.getScoAfContrato() != null) {
			vlrItemContratoServico = this.getProcessamentoCustoUtils().getSigAtividadeServicosDAO()
					.buscaDetalheValorItemContrato(sigProcessamentoCusto, cctCodigo, calculoAtividade.getScoAfContrato().getSeq(), null, null);

		} else {
			vlrItemContratoServico = this
					.getProcessamentoCustoUtils()
					.getSigAtividadeServicosDAO()
					.buscaDetalheValorItemContrato(sigProcessamentoCusto, cctCodigo, null, calculoAtividade.getAutorizacaoForn().getNumero(),
							calculoAtividade.getServico().getCodigo());

		}
		return vlrItemContratoServico;
	}

	/**
	 * Efetua a válidação da mudança de um item de contrato ou uma af
	 * verificação utilizada no passo 05
	 * @return se houve ou não mudança do item de contrato
	 * @author jgugel
	 */
	private boolean verificaMudouItemOUCentroCusto(String seqCorrrente, ServicoAtividadeObjetoCustoVO servicoAtividadeObjeto) {
		return seqCorrrente != null
				&& !seqCorrrente.equals(servicoAtividadeObjeto.getAfcoSeq() + " " + servicoAtividadeObjeto.getAfnNumero() + " "
						+ servicoAtividadeObjeto.getSrvCodigo() + " " + servicoAtividadeObjeto.getCctCodigo());
	}

	/**
	 * Metodo interno para montar o seq das afs
	 * @author jgugel
	 */
	private String montaSeqItemComCentroCusto(ServicoAtividadeObjetoCustoVO servicoAtividadeObjeto) {
		return servicoAtividadeObjeto.getAfcoSeq() + " " + servicoAtividadeObjeto.getAfnNumero() + " " + servicoAtividadeObjeto.getSrvCodigo() + " "
				+ servicoAtividadeObjeto.getCctCodigo();
	}

	/**
	 * Efetua o cadastro do Calculo da Atividade.	
	 * @param rapServidores
	 * @param vo
	 * @return Calculo atividade cadastrado
	 * @throws ApplicationBusinessException
	 * @author jgugel
	 */
	private SigCalculoAtividadeServico cadastrarCalculoAtividadeServico(RapServidores rapServidores, ServicoAtividadeObjetoCustoVO vo)
			throws ApplicationBusinessException {
		//Passo 04
		SigCalculoAtividadeServico sigCalculoAtividadeServico = new SigCalculoAtividadeServico();
		sigCalculoAtividadeServico.setCriadoEm(new Date());
		sigCalculoAtividadeServico.setRapServidores(rapServidores);
		sigCalculoAtividadeServico.setSigCalculoComponentes(this.getProcessamentoCustoUtils().getSigCalculoComponenteDAO()
				.obterPorChavePrimaria((Integer) vo.getCmtSeq()));
		sigCalculoAtividadeServico.setSigAtividades(this.getProcessamentoCustoUtils().getSigAtividadesDAO()
				.obterPorChavePrimaria((Integer) vo.getTvdSeq()));

		SigAtividadeServicos atividadeServicos = this.getProcessamentoCustoUtils().getSigAtividadeServicosDAO()
				.obterPorChavePrimaria((Integer) vo.getArvSeq());
		sigCalculoAtividadeServico.setSigAtividadeServicos(atividadeServicos);
		sigCalculoAtividadeServico.setSigDirecionadores(atividadeServicos.getSigDirecionadores());

		sigCalculoAtividadeServico.setPeso(vo.getPesoOc());
		sigCalculoAtividadeServico.setVlrItemContrato(BigDecimal.ZERO);
		if (vo.getAfcoSeq() != null) {
			sigCalculoAtividadeServico.setScoAfContrato(this.getProcessamentoCustoUtils().getSiconFacade().obterAfContratosById(vo.getAfcoSeq()));
			//Adicionar essa parte
			VSigAfsContratosServicos servico = this.getProcessamentoCustoUtils().getCustosSigFacade().obterAfPorId(vo.getAfcoSeq());
			sigCalculoAtividadeServico.setServico(this.getProcessamentoCustoUtils().getComprasFacade().obterServicoPorId(servico.getCodigoServico()));
		} else {
			sigCalculoAtividadeServico.setAutorizacaoForn(this.getProcessamentoCustoUtils().getAutFornecimentoFacade().obterAfByNumero(vo.getAfnNumero()));
			sigCalculoAtividadeServico.setServico(this.getProcessamentoCustoUtils().getComprasFacade().obterServicoPorId(vo.getSrvCodigo()));
		}
		this.getProcessamentoCustoUtils().getSigCalculoAtividadeServicoDAO().persistir(sigCalculoAtividadeServico);
		return sigCalculoAtividadeServico;
	}
}
