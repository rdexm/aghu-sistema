package br.gov.mec.aghu.sig.custos.processamento.diario.business;

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
import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioIndContagem;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.model.SigCategoriaRecurso;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoExceptionCode;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoDiarioContagemBusiness;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Estória do Usuário #27083 - Contagem de itens de custo do paciente 
 *  
 * Esse processamento efetua a contagem dos objetos de custos consumidos pelo paciente durante sua internação. 
 * Ao final do mês cada internação de um paciente terá todos os itens (objetos de custo) que compõem o seu custo do mês contabilizados
 * 
 * @author jgugel
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoDiarioContagemICP.class)
public class ProcessamentoDiarioContagemICP extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = -4866342497225330580L;

	@Override
	public String getTitulo() {
		return "Executa o processamento diario - contagem de itens de custo do paciente.";
	}

	@Override
	public String getNome() {
		return "processamentoDiarioContagemICPRN - contagemItensCustoPaciente";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 27;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		
		// Etapa 3
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_CONTAGEM_PERMANENCIA_INICIADA");

		// Etapa 4
		//Todas as internações para as quais houve o registro da alta, mas ainda não houve o cálculo de permanência em unidade de internação, especialidade e equipe
		this.dispararContagens(this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().buscarAltasInternacao(sigProcessamentoCusto),
				sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, true);

		// FE01
		Date dataAtual = new Date();
		if (DateUtil.validaDataMaiorIgual(dataAtual, sigProcessamentoCusto.getDataFim())){
			//Depois do último dia do mês (provavelmente no dia 15 do próximo mês), todos os pacientes internados, com alta ou não, devem ter suas permanências calculadas. 
			//Isso ocorre, porque internações que ocorrem parte em um mês e parte em outro, irá gerar registros de cálculos dos seus custos em cada uma das competências.
			this.dispararContagens(this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().buscarTodasInternacoes(sigProcessamentoCusto), sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, false);
		}

		//Etapa etapa 7
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_CONTAGEM_PERMANENCIA_FINALIZADA");
	}

	private void dispararContagens(List<SigCalculoAtdPaciente> retorno, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, Boolean alta) throws ApplicationBusinessException {
		
		Map<DominioIndContagem, SigCategoriaConsumos> categoriasConsumos = new HashMap<DominioIndContagem, SigCategoriaConsumos>();
		for(DominioIndContagem tipo : DominioIndContagem.values()){
			categoriasConsumos.put(tipo, this.obterCategoriaConsumoPorIndicadorContagem(tipo, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos));
		}
		
		Map<Integer, SigCategoriaRecurso> categoriasRecursos = new HashMap<Integer, SigCategoriaRecurso>();
		for(Integer seq = 1;  seq <= 4; seq++){
			categoriasRecursos.put(seq,  this.getProcessamentoCustoUtils().getSigCategoriaRecursoDAO().obterPorChavePrimaria(seq));
		}
		
		Map<AghuParametrosEnum, Object> parametros = new HashMap<AghuParametrosEnum, Object>(); 
		this.carregarParametrosUtilizadosProcessamentoDiario(parametros, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
		
		List<AinMovimentosInternacao> movimentosInternacoes = null;
		for (SigCalculoAtdPaciente calculoAtdPaciente : retorno) {
			
			//Busca todos os movimentos de internação do atendimento até a data de fim da competência processada
			movimentosInternacoes = this.getProcessamentoCustoUtils().getInternacaoFacade().buscarMovimentosInternacao(calculoAtdPaciente.getInternacao().getSeq(), sigProcessamentoCusto.getDataFim());

			//Estória do Usuário #27084 - Contagem de permanência
			this.executarContagem(this.getProcessamentoCustoUtils().getProcessamentoDiarioContagemICPPermanenciaRN(), sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, calculoAtdPaciente, movimentosInternacoes, categoriasConsumos, categoriasRecursos, parametros, alta);
			
			//Estória do Usuário #27488 - Contagem de exames 			
			this.executarContagem(this.getProcessamentoCustoUtils().getProcessamentoDiarioContagemICPExamesRN(), sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, calculoAtdPaciente, movimentosInternacoes, categoriasConsumos, categoriasRecursos, parametros, alta);

			//Estória do Usuário #27654 - Contagem de medicamentos
			this.executarContagem(this.getProcessamentoCustoUtils().getProcessamentoDiarioContagemICPMedicamentosRN(), sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, calculoAtdPaciente, movimentosInternacoes, categoriasConsumos, categoriasRecursos, parametros, alta);

			//Estória do Usuário #27848 - Contagem de cuidados de enfermagem
			this.executarContagem(this.getProcessamentoCustoUtils().getProcessamentoDiarioContagemICPCuidadosEnfermagemRN(), sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, calculoAtdPaciente, movimentosInternacoes, categoriasConsumos, categoriasRecursos, parametros, alta);
			
			//Estória do Usuário #28062 - Contagem de procedimentos dispararContagenscirúrgicos
			this.executarContagem(this.getProcessamentoCustoUtils().getProcessamentoDiarioContagemICProdCirurgicosRN(), sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, calculoAtdPaciente, movimentosInternacoes, categoriasConsumos, categoriasRecursos, parametros, alta);
			
			//Estória do Usuário #28384 - Contagem de consultorias		
			this.executarContagem(this.getProcessamentoCustoUtils().getProcessamentoDiarioContagemICPConsultoriasON(), sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, calculoAtdPaciente, movimentosInternacoes, categoriasConsumos, categoriasRecursos, parametros, alta);
			
			//Estória de Usuário #28385
			this.executarContagem(this.getProcessamentoCustoUtils().getProcessamentoDiarioContagemICPOrtesesProteses(), sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, calculoAtdPaciente, movimentosInternacoes, categoriasConsumos, categoriasRecursos, parametros, alta);
			
			//Estória de Usuário #28394
			this.executarContagem(this.getProcessamentoCustoUtils().getProcessamentoDiarioContagemICPDietas(), sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, calculoAtdPaciente, movimentosInternacoes, categoriasConsumos, categoriasRecursos, parametros, alta);
			
			//Estória do Usuário #28395			
			this.executarContagem(this.getProcessamentoCustoUtils().getProcessamentoDiarioContagemICPHemoterapiasON(), sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, calculoAtdPaciente, movimentosInternacoes, categoriasConsumos, categoriasRecursos, parametros, alta);
		
			//Estória do Usuário #28396
			this.executarContagem(this.getProcessamentoCustoUtils().getProcessamentoDiarioContagemICPNutricaoParenteralON(), sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, calculoAtdPaciente, movimentosInternacoes, categoriasConsumos, categoriasRecursos, parametros, alta);
			
			//Estória do Usuário #28397
			this.executarContagem(this.getProcessamentoCustoUtils().getProcessamentoDiarioContagemICPProcedimentosEspeciaisON(), sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, calculoAtdPaciente, movimentosInternacoes, categoriasConsumos, categoriasRecursos, parametros, alta);
		}	
	}
	
	/** 
	 * Método genérico responsável pela execução do processamento. 
	 * 
	 * @author rmalvezzi
	 * @param <T> 								Qual o tipo/classe do processamento.
	 * @param obj 								Objeto a ser executada a etapa de processamento.
	 * @param processamentoAtual				O processamento atual.
	 * @param servidor							O objeto que representa o usuário logado.
	 * @param tipoObjetoCusto					Null se não possuir um tipo associado.
	 * @return 									True se o processamento foi executado com sucesso.
	 * @throws ApplicationBusinessException	Exceção lançada se for para abortar o processamento.
	 */
	private <T extends ProcessamentoDiarioContagemBusiness> void executarContagem(T obj, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos, SigCalculoAtdPaciente calculoAtdPaciente, List<AinMovimentosInternacao> movimentosInternacoes, Map<DominioIndContagem, SigCategoriaConsumos> categoriasConsumos, Map<Integer, SigCategoriaRecurso> categoriasRecursos, Map<AghuParametrosEnum, Object> parametros, Boolean alta) throws ApplicationBusinessException {
		obj.executarContagem(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, calculoAtdPaciente, movimentosInternacoes, categoriasConsumos, categoriasRecursos, parametros, alta);
	}
	
	public void carregarParametrosUtilizadosProcessamentoDiario(Map<AghuParametrosEnum, Object> parametros, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException{
		
		this.carregarObjetoCusto(AghuParametrosEnum.P_AGHU_SIG_SEQ_MEDICACOES_PACIENTE, parametros, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
		
		this.carregarAtividade(AghuParametrosEnum.P_AGHU_SIG_SEQ_MEDICAR_PACIENTE_PROCESSAMENTO_DIARIO, parametros, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
		
		this.carregarAtividade(AghuParametrosEnum.P_AGHU_SIG_SEQ_REALIZAR_CIRURGIA_NO_PACIENTE, parametros, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
		
		this.carregarObjetoCusto(AghuParametrosEnum.P_AGHU_SIG_SEQ_CONSULTORIAS_MEDICAS, parametros, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
		
		this.carregarObjetoCusto(AghuParametrosEnum.P_AGHU_SIG_SEQ_ORTESE_PROTESE, parametros, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);

		this.carregarAtividade(AghuParametrosEnum.P_AGHU_SIG_SEQ_UTILIZAR_ORTESE_PROTESE_NO_PACIENTE, parametros, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
	}

	public void carregarObjetoCusto(AghuParametrosEnum parametro, Map<AghuParametrosEnum, Object> parametros,  SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException{
		BigDecimal seqObjetoCusto = this.getProcessamentoCustoUtils().getParametroFacade().obterAghParametro(parametro).getVlrNumerico();
		SigObjetoCustoVersoes objetoCustoVersao = this.getProcessamentoCustoUtils().getSigObjetoCustoVersoesDAO().obterSigObjetoCustoVersaoAtivaPorParametro(seqObjetoCusto);
		
		if(objetoCustoVersao != null){
			parametros.put(parametro, objetoCustoVersao);
		}
		else{
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_ERRO_OBJETO_CUSTO_PARAMETRIZADO_NAO_ENCONTRADO", parametro, seqObjetoCusto);
			throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_OBJETO_CUSTO_PARAMETRIZADO_NAO_ENCONTRADO, seqObjetoCusto);
		}
	}
	
	public void carregarAtividade(AghuParametrosEnum parametro, Map<AghuParametrosEnum, Object> parametros, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException{
		Integer seqAtividade = this.getProcessamentoCustoUtils().getParametroFacade().obterAghParametro(parametro).getVlrNumerico().intValue();
		SigAtividades atividade = this.getProcessamentoCustoUtils().getSigAtividadesDAO().obterPorChavePrimaria(seqAtividade.intValue());
		
		if(atividade != null){
			parametros.put(parametro, atividade);
		}
		else{
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_ERRO_ATIVIDADE_PARAMETRIZADA_NAO_ENCONTRADA", parametro, seqAtividade);
			throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_ATIVIDADE_PARAMETRIZADA_NAO_ENCONTRADA, seqAtividade);
		}
	}	
}