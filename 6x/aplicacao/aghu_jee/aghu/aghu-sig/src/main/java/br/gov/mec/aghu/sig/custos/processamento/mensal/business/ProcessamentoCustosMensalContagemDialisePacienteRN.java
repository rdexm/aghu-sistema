package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import br.gov.mec.aghu.model.AinMovimentosInternacao;
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
import br.gov.mec.aghu.sig.custos.vo.CuidadosPrescricaoDialiseVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * #32218 – Contagem de cuidados de diálise por tipo de atendimento do paciente
 * 
 * @author rogeriovieira
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalContagemDialisePacienteRN.class)
public class ProcessamentoCustosMensalContagemDialisePacienteRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = 7811623261611687590L;
	
	@Override
	public String getTitulo() {
		return "Contagem de cuidados de diálise por tipo de atendimento do paciente"; 
	}

	@Override
	public String getNome() {
		return "ProcessamentoCustosMensalContagemDialisePacienteRN";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 30;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		
		//Passo 1
		SigCategoriaConsumos categoriaConsumo = this.obterCategoriaConsumoPorIndicadorContagem(DominioIndContagem.DC, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
		
		//Passo 2
		List<CuidadosPrescricaoDialiseVO> lista = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO()
				.buscarCuidadosPrescricaoDialise(sigProcessamentoCusto.getDataInicio(), sigProcessamentoCusto.getDataFim());
		//Passo 3
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,  DominioEtapaProcessamento.C , "MENSAGEM_DADOS_CUIDADO_DIALISE_OBTIDO_SUCESSO", sigProcessamentoCusto.getCompetenciaMesAno());
		
		if (ProcessamentoCustoUtils.verificarListaNaoVazia(lista)) {
			this.processarCuidadosPrescricaoDialise(lista, categoriaConsumo, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
		} else {
			//FE02
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,  DominioEtapaProcessamento.C , "MENSAGEM_SEM_DADOS_CUIDADO_DIALISE", sigProcessamentoCusto.getCompetenciaMesAno());
		}
	}

	private void processarCuidadosPrescricaoDialise(List<CuidadosPrescricaoDialiseVO> lista, SigCategoriaConsumos categoriaConsumo, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {
		
		Short pConvenioSus = this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS).getVlrNumerico().shortValue();
		
        Short pTipoGrupoContaSus = this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS).getVlrNumerico().shortValue();
		
		List<AghAtendimentos> listaAtendimentos;
		AghAtendimentos atendimento;
		AinMovimentosInternacao ultimoMovimentoInternacao;
		SigCalculoAtdPaciente calculoPaciente;
		FccCentroCustos centroCusto;
		AghEspecialidades especialidade;
		RapServidores equipe;
		SigCalculoAtdPermanencia permanenciaUnidadeFuncional, permanenciaEspecialidade, permanenciaEquipe;
		DominioCalculoPermanencia tipoUnidadeFuncional;
		BigDecimal quantidadeCuidados;
		SigObjetoCustoVersoes objetoCustoVersao;
		FatProcedHospInternos phi;
		MptTratamentoTerapeutico tratamentoTerapeutico;
		
		Map<String, Long> cacheAprazamentos = new HashMap<String, Long>();
		
		//Passo 4
		for (CuidadosPrescricaoDialiseVO vo : lista) {
			
			tratamentoTerapeutico = this.getProcessamentoCustoUtils().getProcedimentoTerapeuticoFacade().obterTratamentoTerapeutico(vo.getTratamentosTerapeuticosSeq());
			
			//Passo 5
			listaAtendimentos = this.getProcessamentoCustoUtils().getAghuFacade().buscarAtendimentos(vo.getCodPaciente(),vo.getDataPrevisaoExecucao());
			
			
			if(ProcessamentoCustoUtils.verificarListaNaoVazia(listaAtendimentos)){
				
				atendimento = listaAtendimentos.get(0);
				
				//Passo 6
				ultimoMovimentoInternacao = this.obterUltimoMovimentoInternacao(atendimento.getInternacao().getSeq(), vo.getDataPrevisaoExecucao());
				if(ultimoMovimentoInternacao == null){
					continue;
				}
				
				centroCusto = ultimoMovimentoInternacao.getUnidadeFuncional().getCentroCusto();
				tipoUnidadeFuncional = DominioCalculoPermanencia.UI;
			}
			else{
				atendimento = null;
				ultimoMovimentoInternacao = null;
				centroCusto = this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorUnidadeFuncional(tratamentoTerapeutico.getUnfSeq());
				tipoUnidadeFuncional = DominioCalculoPermanencia.UA;
			}
			
			//Passo 7,8,9
			calculoPaciente = this.manterCalculoPaciente(vo, atendimento, tratamentoTerapeutico, sigProcessamentoCusto, rapServidores, pConvenioSus, pTipoGrupoContaSus);
			
			//Passo 10
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,  DominioEtapaProcessamento.C , "MENSAGEM_ATENDIMENTO_CUIDADO_DIALISE_INCLUIDO", vo.getAtdPaciente());
			
			//Passo 11
			especialidade = tratamentoTerapeutico.getEspecialidade();
			equipe = tratamentoTerapeutico.getServidorResponsavel();
			
			//Passo 12
			permanenciaUnidadeFuncional = this.manterPermanencia(calculoPaciente, tipoUnidadeFuncional, rapServidores, centroCusto, null, null, sigProcessamentoCusto);
			
			//Passo 13
			permanenciaEspecialidade = this.manterPermanencia(calculoPaciente, DominioCalculoPermanencia.SM, rapServidores, null, especialidade, null, sigProcessamentoCusto);
			
			//Passo 14
			permanenciaEquipe =  this.manterPermanencia(calculoPaciente, DominioCalculoPermanencia.EQ, rapServidores, null, null, equipe, sigProcessamentoCusto);
			
			//Passo 15
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,  DominioEtapaProcessamento.C , "MENSAGEM_PERMANENCIA_CUIDADO_DIALISE_ATUALIZADO", centroCusto.getDescricao(), especialidade.getNomeEspecialidade(), equipe.getPessoaFisica().getNome());
			
			//Passo 16 
			quantidadeCuidados = this.calcularQuantidadeCuidados(vo, cacheAprazamentos);
			
			
			if (vo.getOcvSeq() != null) {
				objetoCustoVersao = this.getProcessamentoCustoUtils().getSigObjetoCustoVersoesDAO().obterPorChavePrimaria(vo.getOcvSeq());
				phi = null;
			}
			else {
				phi = this.getProcessamentoCustoUtils().getFaturamentoFacade().obterProcedimentoHospitalarInterno(vo.getPhiSeq());
				objetoCustoVersao = null;
			}
			
			// Passo 17, 18, 19 (Usando passo 12)
			this.manterCalculoConsumo(permanenciaUnidadeFuncional, quantidadeCuidados, objetoCustoVersao, phi, centroCusto, categoriaConsumo, rapServidores);
			
			//Passo 17, 18, 19 (Usando passo 13)
			this.manterCalculoConsumo(permanenciaEspecialidade, quantidadeCuidados, objetoCustoVersao, phi, centroCusto, categoriaConsumo, rapServidores);
			
			//Passo 17, 18, 19 (Usando passo 14)
			this.manterCalculoConsumo(permanenciaEquipe, quantidadeCuidados, objetoCustoVersao, phi, centroCusto, categoriaConsumo, rapServidores);
			
			//Passo 20 (Usando 12,13,14)
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,  DominioEtapaProcessamento.C , "MENSAGEM_QUANTIDADE_PRESCRICAO_CUIDADO_DIALISE_ATUALIZADO", vo.getAtdPaciente(), centroCusto.getDescricao(), especialidade.getNomeEspecialidade(), equipe.getPessoaFisica().getNome());
		
			this.commitProcessamentoCusto();
		}
	}
	
	private SigCalculoAtdPaciente manterCalculoPaciente(CuidadosPrescricaoDialiseVO vo, AghAtendimentos atendimento, MptTratamentoTerapeutico tratamentoTerapeutico, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidor, Short pConvenioSus, Short pTipoGrupoContaSus){
		
		//Passo 7
		List<SigCalculoAtdPaciente> listaCalculoPaciente = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO() .buscarCalculoAtendimentoPaciente(vo.getAtdPaciente(), sigProcessamentoCusto, atendimento != null ?  atendimento.getInternacao() : null);
		
		SigCalculoAtdPaciente calculoPaciente;
		
		//Passo 8
		if (ProcessamentoCustoUtils.verificarListaNaoVazia(listaCalculoPaciente)) {
			calculoPaciente = listaCalculoPaciente.get(0);
			calculoPaciente.setTratamentoTerapeutico(tratamentoTerapeutico);
			this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().atualizar(calculoPaciente);
		} 
		//Passo 9
		else {
			calculoPaciente = new SigCalculoAtdPaciente();
			calculoPaciente.setProcessamentoCusto(sigProcessamentoCusto);
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
				calculoPaciente.setSituacaoCalculoPaciente(DominioSituacaoCalculoPaciente.A);//ambulatorial
			}
			
			calculoPaciente.setAtendimento(this.getProcessamentoCustoUtils().getAghuFacade().obterAghAtendimentoPorChavePrimaria(vo.getAtdPaciente()));
			calculoPaciente.setIndPacientePediatrico(calculoPaciente.getAtendimento().getIndPacPediatrico());
			calculoPaciente.setRapServidores(rapServidor);
			calculoPaciente.setCriadoEm(new Date());
			this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().persistir(calculoPaciente);
			this.inserirCidsProcedimentos(calculoPaciente, rapServidor, pConvenioSus, pTipoGrupoContaSus);
			return calculoPaciente;
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
	
	private void manterCalculoConsumo(SigCalculoAtdPermanencia permanencia, BigDecimal quantidadeCuidados, SigObjetoCustoVersoes objetoCustoVersao, FatProcedHospInternos phi, FccCentroCustos centroCusto, SigCategoriaConsumos categoriaConsumo, RapServidores rapServidores){
		
		SigCalculoAtdConsumo  consumo = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarItemConsumo(permanencia, objetoCustoVersao, phi);
		
		if (consumo == null) {
			consumo = new SigCalculoAtdConsumo();
			consumo.setCalculoAtividadePermanencia(permanencia);
			consumo.setSigObjetoCustoVersoes(objetoCustoVersao);
			consumo.setProcedHospInterno(phi);
			consumo.setQtde( quantidadeCuidados);
			consumo.setRapServidores(rapServidores);
			consumo.setCriadoEm(new Date());
			consumo.setCentroCustos(centroCusto);
			consumo.setCategoriaConsumo(categoriaConsumo);
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().persistir(consumo);
		} else {
			consumo.setQtde(consumo.getQtde().add(quantidadeCuidados));
			consumo.setCategoriaConsumo(categoriaConsumo);
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().atualizar(consumo);
		}
	}
	
	private BigDecimal calcularQuantidadeCuidados(CuidadosPrescricaoDialiseVO vo, Map<String, Long> aprazamentos) {
		Long calculoNumeroVezesAprazamento = this.calcularNumeroVezesAprazamento(vo.getFrequenciaAprazamentoSeq(), vo.getFrequencia(), aprazamentos);
		return new BigDecimal(Math.round(calculoNumeroVezesAprazamento) * vo.getQuantidadeCuidados());
	}
}
