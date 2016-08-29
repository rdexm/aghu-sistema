package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioCalculoPermanencia;
import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioIndContagem;
import br.gov.mec.aghu.dominio.DominioSituacaoCalculoPaciente;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MptTratamentoTerapeutico;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoAtdConsumo;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigCalculoAtdPermanencia;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.vo.ContagemQuimioterapiaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * #31046
 * Esta contagem tem o objetivo de, na execução do processamento mensal, verificar quais pacientes tiveram cuidados 
 * quimioterápicos prescritos durante o seu tratamento. Conforme o atendimento do paciente será associado às unidades 
 * de atendimento (ambulatorial ou internação), especialidades e equipes pelos quais o paciente passou.
 * 
 * @author rmalvezzi
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalContagemCuidadosQuimioterapiaRN.class)
public class ProcessamentoCustosMensalContagemCuidadosQuimioterapiaRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = -1555834042773485374L;

	@Override
	public String getTitulo() {
		return "Contagem de cuidados de quimioterapias por tipo de atendimento do paciente";
	}

	@Override
	public String getNome() {
		return "ProcessamentoCustosMensalContagemCuidadosQuimioterapiaRN";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 29;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {

		//Passo 1
		SigCategoriaConsumos categoriaConsumo = this.obterCategoriaConsumoPorIndicadorContagem(DominioIndContagem.QC, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
		
		//Passo 2
		List<ContagemQuimioterapiaVO> lista = this.getProcessamentoCustoUtils().getAghuFacade().buscarCuidadosPrescricaoQuimioterapia(sigProcessamentoCusto.getDataInicio(), sigProcessamentoCusto.getDataFim());
		
		if (ProcessamentoCustoUtils.verificarListaNaoVazia(lista)) {			
			//Passo 3
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "RESULTADOS_CUIDADOS_QUIMIOTERAPIA_REALIZADOS", sigProcessamentoCusto.getCompetenciaMesAno());
		} else {
			//[FE01]
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "SEM_RESULTADOS_CUIDADOS_QUIMIOTERAPIA", sigProcessamentoCusto.getCompetenciaMesAno());			
			return;
		}
		
		Short pConvenioSus = this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS).getVlrNumerico().shortValue();
		
        Short pTipoGrupoContaSus = this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS).getVlrNumerico().shortValue();
		
		MptTratamentoTerapeutico tratamentoTerapeutico;
		AghAtendimentos atendimento;
		SigCalculoAtdPermanencia permanenciaUnidadeFuncional, permanenciaEspecialidade, permanenciaEquipe;
		AghUnidadesFuncionais unidadeFuncional;
		List<AghAtendimentos> atendimentos;
		SigCalculoAtdPaciente calculoPaciente;
		DominioCalculoPermanencia tipoUnidadeFuncional;
		FccCentroCustos centroCusto;
		AghEspecialidades especialidade;
		RapServidores equipe;
		SigObjetoCustoVersoes objetoCustoVersao;
		FatProcedHospInternos phi;
		
		//Passo 4
		for (ContagemQuimioterapiaVO vo : lista) {

			tratamentoTerapeutico = this.getProcessamentoCustoUtils().getProcedimentoTerapeuticoFacade().obterTratamentoTerapeutico(vo.getTratamentoTerapeuticos());
			
			//Passo 5
			unidadeFuncional = this.getProcessamentoCustoUtils().getAghuFacade().buscarUnidadeInternacaoAtiva(vo.getUnidade());
			
			if(unidadeFuncional != null){
				//Passo 6
				atendimentos = this.getProcessamentoCustoUtils().getAghuFacade().buscarAtendimentos(vo.getPaciente(), vo.getDtPrevExecucao());
				if(!ProcessamentoCustoUtils.verificarListaNaoVazia(atendimentos)){
					continue;//Mesmo sendo unidade de internação, não encontrou atendimento para a data informada
				}
				atendimento = atendimentos.get(0);
				tipoUnidadeFuncional = DominioCalculoPermanencia.UI; 
			}
			else{
				atendimento = null;
				tipoUnidadeFuncional = DominioCalculoPermanencia.UA;
			}
			
			// Passo 7, 8 e 9			
			calculoPaciente = this.manterCalculoPaciente(vo.getAtdPaciente(), atendimento, tratamentoTerapeutico, sigProcessamentoCusto, rapServidores, pConvenioSus, pTipoGrupoContaSus);
			
			//Passo 10
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "QUIMIO_ATENDIMENTO_PACIENTE", vo.getAtdPaciente());
			
			centroCusto = this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(vo.getCentroCusto());
			
			//Passo 11
			permanenciaUnidadeFuncional = this.manterPermanencia(calculoPaciente, tipoUnidadeFuncional, rapServidores, centroCusto, null, null, sigProcessamentoCusto);
			
			//Passo 12
			especialidade = tratamentoTerapeutico.getEspecialidade();
			equipe = tratamentoTerapeutico.getServidorResponsavel();
			
			//Passo 13
			permanenciaEspecialidade = this.manterPermanencia(calculoPaciente, DominioCalculoPermanencia.SM, rapServidores, null, especialidade, null, sigProcessamentoCusto);
			
			//Passo 14
			permanenciaEquipe = this.manterPermanencia(calculoPaciente, DominioCalculoPermanencia.EQ, rapServidores, null, null, equipe, sigProcessamentoCusto);
			
			//Passo 15
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,DominioEtapaProcessamento.C, "QUIMIO_TEMPO_PERMANENCIA_BASA", vo.getAtdPaciente(), centroCusto.getDescricao(), especialidade.getNomeEspecialidade(), equipe.getPessoaFisica().getNome());
			
			//Passo 16
			BigDecimal quantidadeCuidados = BigDecimal.valueOf(this.calcularNumeroVezesAprazamento(vo.getFreqAprazamento().shortValue(), vo.getFrequencia()) * vo.getQtdCuidados());

			if (vo.getOcv() != null) {
				objetoCustoVersao = this.getProcessamentoCustoUtils().getSigObjetoCustoVersoesDAO().obterPorChavePrimaria(vo.getOcv());
				phi = null;
			} else {
				phi = this.getProcessamentoCustoUtils().getFaturamentoFacade().obterFatProcedHospInternosPorChavePrimaria(vo.getPhi());
				objetoCustoVersao = null;
			}
			
			//PassoS 17, 18 e 19
			this.manterCalculoConsumo(permanenciaUnidadeFuncional, quantidadeCuidados, objetoCustoVersao, phi, centroCusto, categoriaConsumo, rapServidores);

			//Passo 21
			this.manterCalculoConsumo(permanenciaEspecialidade, quantidadeCuidados, objetoCustoVersao, phi, centroCusto, categoriaConsumo, rapServidores);
			this.manterCalculoConsumo(permanenciaEquipe, quantidadeCuidados, objetoCustoVersao, phi, centroCusto, categoriaConsumo, rapServidores);
		
			//Passo 20
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "QUANTIDADE_QUIMIO_ATENDIMENTO_ATUALIZADA",  vo.getAtdPaciente(), centroCusto.getDescricao(), especialidade.getNomeEspecialidade(), equipe.getPessoaFisica().getNome());
			
			this.commitProcessamentoCusto();
		}
	}
	
	private SigCalculoAtdPaciente manterCalculoPaciente(Integer atdPaciente, AghAtendimentos atendimento, MptTratamentoTerapeutico tratamentoTerapeutico, SigProcessamentoCusto processamentoCusto, RapServidores rapServidores, Short pConvenioSus, Short pTipoGrupoContaSus) {

		//Passo 7
		List<SigCalculoAtdPaciente> listaCalculoPaciente = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().buscarCalculoAtendimentoPaciente(atdPaciente, processamentoCusto, atendimento != null ? atendimento.getInternacao() : null);

		//Passo 8			
		SigCalculoAtdPaciente calculoPaciente;
		if (ProcessamentoCustoUtils.verificarListaNaoVazia(listaCalculoPaciente)) {
			calculoPaciente = listaCalculoPaciente.get(0);
			calculoPaciente.setTratamentoTerapeutico(tratamentoTerapeutico);
			this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().atualizar(calculoPaciente);
		} 
		//Passo 9
		else {
			calculoPaciente = new SigCalculoAtdPaciente();
			calculoPaciente.setCriadoEm(new Date());

			calculoPaciente.setProcessamentoCusto(processamentoCusto);
			calculoPaciente.setAtendimento(this.getProcessamentoCustoUtils().getAghuFacade().obterAghAtendimentoPorChavePrimaria(atdPaciente));
			calculoPaciente.setTratamentoTerapeutico(tratamentoTerapeutico);
			
			if (atendimento != null) {
				calculoPaciente.setInternacao(atendimento.getInternacao());
				
				if (atendimento.getDthrFim() == null) {
					calculoPaciente.setSituacaoCalculoPaciente(DominioSituacaoCalculoPaciente.I);
				} else {
					calculoPaciente.setSituacaoCalculoPaciente(DominioSituacaoCalculoPaciente.IA);
				}
			}
			else{
				calculoPaciente.setSituacaoCalculoPaciente(DominioSituacaoCalculoPaciente.A);
			}

			calculoPaciente.setRapServidores(rapServidores);
			calculoPaciente.setIndPacientePediatrico(calculoPaciente.getAtendimento().getIndPacPediatrico());
			this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().persistir(calculoPaciente);
			this.inserirCidsProcedimentos(calculoPaciente, rapServidores, pConvenioSus, pTipoGrupoContaSus);
		}
		return calculoPaciente;
	}
	
	private SigCalculoAtdPermanencia manterPermanencia(SigCalculoAtdPaciente calculoPaciente, DominioCalculoPermanencia tipo, RapServidores servidor,  FccCentroCustos centroCusto, AghEspecialidades especialidade, RapServidores equipe, SigProcessamentoCusto sigProcessamentoCusto ){
		
		SigCalculoAtdPermanencia permanencia = null;
		
		if(centroCusto != null){
			permanencia = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorCentroCusto(calculoPaciente.getAtendimento(), centroCusto, sigProcessamentoCusto);
		}
		else if(especialidade != null){
			permanencia = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEspecialidade(calculoPaciente.getAtendimento(), especialidade, sigProcessamentoCusto);
		}
		else if(equipe != null){
			permanencia = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEquipe(calculoPaciente.getAtendimento(), equipe, sigProcessamentoCusto);
		}

		
		if (permanencia == null) {
			permanencia = new SigCalculoAtdPermanencia();
			permanencia.setCriadoEm(new Date());
			permanencia.setCalculoAtdPaciente(calculoPaciente);
			permanencia.setRapServidores(servidor);
			permanencia.setTipo(tipo);
			permanencia.setTempo(new BigDecimal(1440));
			permanencia.setCentroCustos(centroCusto);
			permanencia.setEspecialidade(especialidade);
			permanencia.setResponsavel(equipe);
			this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().persistir(permanencia);
		}
		
		return permanencia;
	}
	
	private void manterCalculoConsumo(SigCalculoAtdPermanencia permanencia, BigDecimal quantidadeCuidados,  SigObjetoCustoVersoes objetoCustoVersao, FatProcedHospInternos phi, FccCentroCustos centroCusto, SigCategoriaConsumos categoriaConsumo, RapServidores rapServidores) throws ApplicationBusinessException {
		
		//Passo 17
		SigCalculoAtdConsumo consumo = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarItemConsumo(permanencia, objetoCustoVersao, phi);

		//Passo 18
		if (consumo == null) {
			consumo = new SigCalculoAtdConsumo();
			consumo.setCalculoAtividadePermanencia(permanencia);
			consumo.setSigObjetoCustoVersoes(objetoCustoVersao);
			consumo.setProcedHospInterno(phi);
			consumo.setCentroCustos(centroCusto);
			consumo.setQtde(quantidadeCuidados);
			consumo.setRapServidores(rapServidores);
			consumo.setCriadoEm(new Date());
			consumo.setCategoriaConsumo(categoriaConsumo);
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().persistir(consumo);
		} 
		//Passo 18
		else {
			consumo.setQtde(consumo.getQtde().add(quantidadeCuidados));
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().atualizar(consumo);
		}
	}
}
