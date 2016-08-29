package br.gov.mec.aghu.sig.custos.processamento.diario.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioCalculoPermanencia;
import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioIndContagem;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigCalculoAtdPermanencia;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.model.SigCategoriaRecurso;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoDiarioContagemBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Estória do Usuário #27084 - Contagem de permanência por unidade de internação/especialidade/equipe
 * @author rogeriovieira
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessamentoDiarioContagemICPPermanencia extends ProcessamentoDiarioContagemBusiness {


	private static final long serialVersionUID = -8635175515697191884L;

	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			SigCalculoAtdPaciente calculoPaciente, List<AinMovimentosInternacao> movimentosInternacoes, Map<DominioIndContagem, SigCategoriaConsumos> categoriasConsumos, Map<Integer, SigCategoriaRecurso> categoriasRecursos, Map<AghuParametrosEnum, Object> parametros, Boolean alta) throws ApplicationBusinessException {

		// Etapa 1
		List<AinMovimentosInternacao> movimentosInternacaoMesCompetencia = this
				.getProcessamentoCustoUtils()
				.getInternacaoFacade()
				.buscarMovimentosInternacao(calculoPaciente.getInternacao().getSeq(), sigProcessamentoCusto.getDataInicio(),sigProcessamentoCusto.getDataFim(), null);

		//Pré-Processamento
		AinMovimentosInternacao ultimoMovimentoMesAnterior = this.obterUltimoMovimentoInternacao(sigProcessamentoCusto.getDataInicio(), movimentosInternacoes);
		
		if (ultimoMovimentoMesAnterior != null) {
			AinMovimentosInternacao primeiro = new AinMovimentosInternacao();
			primeiro.setInternacao(ultimoMovimentoMesAnterior.getInternacao());
			primeiro.setUnidadeFuncional(ultimoMovimentoMesAnterior.getUnidadeFuncional());
			primeiro.getUnidadeFuncional().setCentroCusto(ultimoMovimentoMesAnterior.getUnidadeFuncional().getCentroCusto());
			primeiro.setEspecialidade(ultimoMovimentoMesAnterior.getEspecialidade());
			primeiro.setServidor(ultimoMovimentoMesAnterior.getServidor());
			primeiro.setDthrLancamento(sigProcessamentoCusto.getDataInicio());

			movimentosInternacaoMesCompetencia.add(0, primeiro);
		}

		if (ProcessamentoCustoUtils.verificarListaNaoVazia(movimentosInternacaoMesCompetencia)) {

			if (!alta) { //Paciente não teve alta durante período de processamento
				
				//Se existirem movimentações será necessário criar um movimento (lógico) na data de fim do processamento utilizando os dados do último movimento, ou seja, da última linha retornada na consulta.
				AinMovimentosInternacao ultimo = movimentosInternacaoMesCompetencia.get(movimentosInternacaoMesCompetencia.size() - 1);

				AinMovimentosInternacao novoUltimo = new AinMovimentosInternacao();
				novoUltimo.setInternacao(ultimo.getInternacao());
				novoUltimo.setUnidadeFuncional(ultimo.getUnidadeFuncional());
				novoUltimo.getUnidadeFuncional().setCentroCusto(ultimo.getUnidadeFuncional().getCentroCusto());
				novoUltimo.setEspecialidade(ultimo.getEspecialidade());
				novoUltimo.setServidor(ultimo.getServidor());
				Calendar c = Calendar.getInstance();
				c.setTime(sigProcessamentoCusto.getDataFim());
				c.add(Calendar.SECOND, 1);
				novoUltimo.setDthrLancamento(c.getTime());

				movimentosInternacaoMesCompetencia.add(novoUltimo);
			}
			
			//Etapa 2
			AinMovimentosInternacao movimentoUnidadeFuncional = movimentosInternacaoMesCompetencia.get(0);
			Map<Object, BigDecimal> mapPermanenciaUnidadeFuncional = new HashMap<Object, BigDecimal>();

			AinMovimentosInternacao movimentoEspecialidade = movimentosInternacaoMesCompetencia.get(0);
			Map<Object, BigDecimal> mapPermanenciaEspecialidade = new HashMap<Object, BigDecimal>();

			AinMovimentosInternacao movimentoEquipe = movimentosInternacaoMesCompetencia.get(0);
			Map<Object, BigDecimal> mapPermanenciaEquipe = new HashMap<Object, BigDecimal>();

			//Etapa 3, 13
			for (AinMovimentosInternacao ainMovimentosInternacao : movimentosInternacaoMesCompetencia) {

				//Etapa 4
				if (ainMovimentosInternacao.getUnidadeFuncional() != null
						&& !ainMovimentosInternacao.getUnidadeFuncional().getCentroCusto().equals(movimentoUnidadeFuncional.getUnidadeFuncional().getCentroCusto())) {

					//FE02
					this.atualizaPermanencia(movimentoUnidadeFuncional, ainMovimentosInternacao, mapPermanenciaUnidadeFuncional, movimentoUnidadeFuncional.getUnidadeFuncional().getCentroCusto());

					//Etapa 6
					this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_TEMPO_PERMANENCIA_UI_ATUALIZADO",
							movimentoUnidadeFuncional.getInternacao().getSeq(),
							movimentoUnidadeFuncional.getUnidadeFuncional().getCentroCusto().getDescricao());

					movimentoUnidadeFuncional = ainMovimentosInternacao;
				}

				//Etapa 7
				if (ainMovimentosInternacao.getEspecialidade() != null
						&& !ainMovimentosInternacao.getEspecialidade().equals(movimentoEspecialidade.getEspecialidade())) {

					//FE04
					this.atualizaPermanencia(movimentoEspecialidade, ainMovimentosInternacao, mapPermanenciaEspecialidade,
							movimentoEspecialidade.getEspecialidade());

					//Etapa 9
					this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_TEMPO_PERMANENCIA_ESPECIALIDADE_ATUALIZADO", movimentoEspecialidade.getInternacao().getSeq(), movimentoEspecialidade.getEspecialidade().getNomeEspecialidade());

					movimentoEspecialidade = ainMovimentosInternacao;
				}

				//Etapa 10
				if (ainMovimentosInternacao.getServidor() != null
						&& !ainMovimentosInternacao.getServidor().equals(movimentoEquipe.getServidor())) {

					//FE06
					this.atualizaPermanencia(movimentoEquipe, ainMovimentosInternacao, mapPermanenciaEquipe, movimentoEquipe.getServidor());

					//Etapa 12
					this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_TEMPO_PERMANENCIA_EQUIPE_ATUALIZADO", movimentoEspecialidade.getInternacao().getSeq(), movimentoEspecialidade.getServidor().getUsuario());

					movimentoEquipe = ainMovimentosInternacao;
				}
			}

			//A útilma movimentação precisa ser regristrada. 

			AinMovimentosInternacao ainMovimentosInternacaoUltimo = movimentosInternacaoMesCompetencia.get(movimentosInternacaoMesCompetencia.size() - 1);
			
			this.atualizaPermanencia(movimentoUnidadeFuncional, ainMovimentosInternacaoUltimo, mapPermanenciaUnidadeFuncional, movimentoUnidadeFuncional.getUnidadeFuncional().getCentroCusto());
			this.atualizaPermanencia(movimentoEspecialidade, ainMovimentosInternacaoUltimo, mapPermanenciaEspecialidade,movimentoEspecialidade.getEspecialidade());
			this.atualizaPermanencia(movimentoEquipe, ainMovimentosInternacaoUltimo, mapPermanenciaEquipe, movimentoEquipe.getServidor());

			//Etapa 5, 8, 11
			this.addPermanencias(calculoPaciente, rapServidores, mapPermanenciaUnidadeFuncional, mapPermanenciaEspecialidade, mapPermanenciaEquipe);

		}
	}

	private SigCalculoAtdPermanencia criaPermanenciaDefault(DominioCalculoPermanencia movimentoCorrente, RapServidores rapServidores, SigCalculoAtdPaciente calculoPaciente, BigDecimal tempo) {
		SigCalculoAtdPermanencia permanencia = new SigCalculoAtdPermanencia();
		permanencia.setTipo(movimentoCorrente);
		permanencia.setCalculoAtdPaciente(calculoPaciente);
		permanencia.setTempo(tempo);
		permanencia.setRapServidores(rapServidores);
		permanencia.setCriadoEm(new Date());
		return permanencia;
	}

	private void addPermanencias(SigCalculoAtdPaciente calculoPaciente, RapServidores rapServidores, Map<Object, BigDecimal> mapPermanenciaUnidadeFuncional, Map<Object, BigDecimal> mapPermanenciaEspecialidade, Map<Object, BigDecimal> mapPermanenciaEquipe) throws ApplicationBusinessException {

		//Etapa 5
		for (Object aghUnidadesFuncionais : mapPermanenciaUnidadeFuncional.keySet()) {
			SigCalculoAtdPermanencia permanencia = this.criaPermanenciaDefault(DominioCalculoPermanencia.UI, rapServidores, calculoPaciente, mapPermanenciaUnidadeFuncional.get(aghUnidadesFuncionais));
			permanencia.setCentroCustos(((FccCentroCustos) aghUnidadesFuncionais));
			this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().persistir(permanencia);
		}
		this.commitProcessamentoCusto();

		//Etapa 8
		for (Object aghEspecialidades : mapPermanenciaEspecialidade.keySet()) {
			SigCalculoAtdPermanencia permanencia = this.criaPermanenciaDefault(DominioCalculoPermanencia.SM, rapServidores, calculoPaciente, mapPermanenciaEspecialidade.get(aghEspecialidades));
			permanencia.setEspecialidade((AghEspecialidades) aghEspecialidades);
			this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().persistir(permanencia);
		}
		this.commitProcessamentoCusto();

		//Etapa 11
		for (Object aghEquipe : mapPermanenciaEquipe.keySet()) {
			SigCalculoAtdPermanencia permanencia = this.criaPermanenciaDefault(DominioCalculoPermanencia.EQ, rapServidores, calculoPaciente, mapPermanenciaEquipe.get(aghEquipe));
			permanencia.setResponsavel((RapServidores) aghEquipe);
			this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().persistir(permanencia);
		}
		this.commitProcessamentoCusto();
	}

	private void atualizaPermanencia(AinMovimentosInternacao movimentoAtual, AinMovimentosInternacao movimentoCorrente, Map<Object, BigDecimal> map, Object chave) {
		
		BigDecimal novoTempo = this.calcularTempoPermanenciaRN01(movimentoCorrente.getDthrLancamento(), movimentoAtual.getDthrLancamento());

		BigDecimal permanenciaUF = map.get(chave);
		if (permanenciaUF != null) {
			novoTempo = novoTempo.add(permanenciaUF);
		}

		map.put(chave, novoTempo.setScale(4, RoundingMode.HALF_UP));
	}

	private BigDecimal calcularTempoPermanenciaRN01(Date tempoNovo, Date tempoAnterior) {
		Long diff = tempoNovo.getTime() - tempoAnterior.getTime();
		return new BigDecimal(diff / 1000.0 / 60);
	}
}
