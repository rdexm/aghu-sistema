package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioIndContagem;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigCalculoAtdReceita;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoExceptionCode;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.InternacaoConvenioVO;
import br.gov.mec.aghu.sig.custos.vo.SigConsumosInternacoesVO;
import br.gov.mec.aghu.sig.custos.vo.SigProcedimentoMedicamentoExameVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * #32240 - Associar custos do paciente x receita Convênio/Particular para internação - custo específico para PHIs
 * 
 * Processamento mensal do módulo de Custos, que irá obter os custos específicos, relativos à internação (medicamentos
 * e exames que possuem PHI associado) dos pacientes atendidos por convênio/particular. 
 * 
 * @author fpalma
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalAssociarPacienteReceitaPHIRN.class)
public class ProcessamentoCustosMensalAssociarPacienteReceitaPHIRN extends ProcessamentoMensalBusiness {


	/**
	 * 
	 */
	private static final long serialVersionUID = 8151798416631838353L;

	@Override
	public String getTitulo() {
		return "Associar custos do paciente x receita convênio/particular para internação. Custo para PHIs.";
	}

	@Override
	public String getNome() {
		return "processamentoCustoMensalAssociarPacienteReceitaPHI - alocarOCPaciente";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 35;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		
        Short pAghuSigCodConvParticular = this.buscarParametroValorNumerico(AghuParametrosEnum.P_AGHU_SIG_COD_CONV_PARTICULAR, "PARAMETRO_NAO_CADASTRADO_P_AGHU_SIG_COD_CONV_PARTICULAR", rapServidores, sigProcessamentoPassos, sigProcessamentoCusto).shortValue();
		
		Short pConvenioSus = this.buscarParametroValorNumerico(AghuParametrosEnum.P_CONVENIO_SUS, "PARAMETRO_NAO_CADASTRADO_P_CONVENIO_SUS", rapServidores, sigProcessamentoPassos, sigProcessamentoCusto).shortValue();
        
        Short pAghuSigConvTipoTabPgtoCodMedic =  this.buscarParametroValorNumerico(AghuParametrosEnum.P_AGHU_SIG_CONV_TIPO_TAB_PGTO_COD_MEDIC, "PARAMETRO_NAO_CADASTRADO_P_AGHU_SIG_CONV_TIPO_TAB_PGTO_COD_MEDIC", rapServidores, sigProcessamentoPassos, sigProcessamentoCusto).shortValue();
		
		Byte pAghuSigConvTipoItemCodMedicDialise = this.buscarParametroValorNumerico(AghuParametrosEnum.P_AGHU_SIG_CONV_TIPO_ITEM_COD_MEDIC_DIALISE, "PARAMETRO_NAO_CADASTRADO_P_AGHU_SIG_CONV_TIPO_ITEM_COD_MEDIC_DIALISE", rapServidores, sigProcessamentoPassos, sigProcessamentoCusto).byteValue();
		
		String[] listaCodExames = this.buscarParametroValorTexto(AghuParametrosEnum.P_AGHU_SIG_CONV_TIPO_TAB_PGTO_LISTA_COD_EXAMES, "PARAMETRO_NAO_CADASTRADO_P_AGHU_SIG_CONV_TIPO_TAB_PGTO_LISTA_COD_EXAMES", rapServidores, sigProcessamentoPassos, sigProcessamentoCusto).split(",");
		List<Short> listaPAghuSigConvTipoTabPgtoListaCodExames = new ArrayList<Short>();
		for(int i=0; i < listaCodExames.length; i++) {
			listaPAghuSigConvTipoTabPgtoListaCodExames.add(Short.valueOf(listaCodExames[i]));
		}
		
		String[] listaItemExames = this.buscarParametroValorTexto(AghuParametrosEnum.P_AGHU_SIG_CONV_TIPO_TIPO_ITEM_LISTA_COD_EXAMES, "PARAMETRO_NAO_CADASTRADO_P_AGHU_SIG_CONV_TIPO_TIPO_ITEM_LISTA_COD_EXAMES", rapServidores, sigProcessamentoPassos, sigProcessamentoCusto).split(",");
		List<Byte> listaPAghuSigConvTipoItemListaCodExames = new ArrayList<Byte>();
		for(int i=0; i < listaItemExames.length; i++) {
			listaPAghuSigConvTipoItemListaCodExames.add(Byte.valueOf(listaItemExames[i]));
		}
		
		Short pAghuSigConvTabPgtoTpInsumoDietas = this.buscarParametroValorNumerico(AghuParametrosEnum.P_AGHU_SIG_CONV_TAB_PGTO_TP_INSUMO_DIETAS, "PARAMETRO_NAO_CADASTRADO_P_AGHU_SIG_CONV_TAB_PGTO_TP_INSUMO_DIETAS", rapServidores, sigProcessamentoPassos, sigProcessamentoCusto).shortValue();
		
		Short pAghuSigConvTipoItemGrupoOrtProtCod =  this.buscarParametroValorNumerico(AghuParametrosEnum.P_AGHU_SIG_CONV_TIPO_ITEM_GRUPO_ORT_PROT_COD, "PARAMETRO_NAO_CADASTRADO_P_AGHU_SIG_CONV_TIPO_ITEM_GRUPO_ORT_PROT_COD", rapServidores, sigProcessamentoPassos, sigProcessamentoCusto).shortValue();
		
		Short pAghuSigConvTipoTabPgtoCodMedicQuimio = this.buscarParametroValorNumerico(AghuParametrosEnum.P_AGHU_SIG_CONV_TIPO_TAB_PGTO_COD_MEDIC_QUIMIO, "PARAMETRO_NAO_CADASTRADO_P_AGHU_SIG_CONV_TIPO_TAB_PGTO_COD_MEDIC_QUIMIO", rapServidores, sigProcessamentoPassos, sigProcessamentoCusto).shortValue();
		
		//Consulta por todos os registro já agrupados pelas categorias de consumo (mostrou que demora menos tempo do que buscar categoria por categoria) 
		Map<Integer, List<SigConsumosInternacoesVO>> mapBuscarConsumosRelacionadoInternacoesDeDetermindadoProcessamento = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarConsumosRelacionadoInternacoesDeDetermindadoProcessamento(sigProcessamentoCusto.getSeq(), sigProcessamentoCusto.getDataInicio(), pConvenioSus, Boolean.FALSE);
		Map<Integer, List<SigConsumosInternacoesVO>> mapBuscarConsumosRelacionadoOcv = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarConsumosRelacionadoOcv(sigProcessamentoCusto.getSeq(), sigProcessamentoCusto.getDataInicio(), pConvenioSus, Boolean.FALSE); 
		Map<Integer, List<SigConsumosInternacoesVO>> mapBuscarConsumosContendoMaterial = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarConsumosContendoMaterial(sigProcessamentoCusto.getSeq(), sigProcessamentoCusto.getDataInicio(), pConvenioSus);
		Set<Integer> pacientesFaturamentoPendenteParaAtualizar = new HashSet<Integer>();
		
		Set<Integer> listaInternacoes = new HashSet<Integer>();
		Map<Integer, List<SigConsumosInternacoesVO>> listaConsumoPorInternacao = new HashMap<Integer, List<SigConsumosInternacoesVO>>();
		Map<Integer, Set<Integer>> listaPhisPorInternacao = new HashMap<Integer, Set<Integer>>();
		Map<Integer, Set<Integer>> listaMateriaisPorInternacao = new HashMap<Integer, Set<Integer>>();
		
		this.executarAssociacao(DominioIndContagem.MD, true,  mapBuscarConsumosRelacionadoInternacoesDeDetermindadoProcessamento, mapBuscarConsumosRelacionadoOcv, mapBuscarConsumosContendoMaterial, listaInternacoes, listaConsumoPorInternacao, listaPhisPorInternacao, listaMateriaisPorInternacao, pacientesFaturamentoPendenteParaAtualizar, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, pAghuSigCodConvParticular, pAghuSigConvTipoTabPgtoCodMedic, pAghuSigConvTipoItemCodMedicDialise, pConvenioSus, listaPAghuSigConvTipoTabPgtoListaCodExames, listaPAghuSigConvTipoItemListaCodExames, pAghuSigConvTabPgtoTpInsumoDietas, pAghuSigConvTipoItemGrupoOrtProtCod, pAghuSigConvTipoTabPgtoCodMedicQuimio);
		this.executarAssociacao(DominioIndContagem.EX, true,  mapBuscarConsumosRelacionadoInternacoesDeDetermindadoProcessamento, mapBuscarConsumosRelacionadoOcv, mapBuscarConsumosContendoMaterial, listaInternacoes, listaConsumoPorInternacao, listaPhisPorInternacao, listaMateriaisPorInternacao, pacientesFaturamentoPendenteParaAtualizar, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, pAghuSigCodConvParticular, pAghuSigConvTipoTabPgtoCodMedic, pAghuSigConvTipoItemCodMedicDialise, pConvenioSus,listaPAghuSigConvTipoTabPgtoListaCodExames, listaPAghuSigConvTipoItemListaCodExames, pAghuSigConvTabPgtoTpInsumoDietas, pAghuSigConvTipoItemGrupoOrtProtCod, pAghuSigConvTipoTabPgtoCodMedicQuimio);
		this.executarAssociacao(DominioIndContagem.CE, true,  mapBuscarConsumosRelacionadoInternacoesDeDetermindadoProcessamento, mapBuscarConsumosRelacionadoOcv, mapBuscarConsumosContendoMaterial, listaInternacoes, listaConsumoPorInternacao, listaPhisPorInternacao, listaMateriaisPorInternacao, pacientesFaturamentoPendenteParaAtualizar, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, pAghuSigCodConvParticular, pAghuSigConvTipoTabPgtoCodMedic, pAghuSigConvTipoItemCodMedicDialise, pConvenioSus,listaPAghuSigConvTipoTabPgtoListaCodExames, listaPAghuSigConvTipoItemListaCodExames, pAghuSigConvTabPgtoTpInsumoDietas, pAghuSigConvTipoItemGrupoOrtProtCod, pAghuSigConvTipoTabPgtoCodMedicQuimio);
		this.executarAssociacao(DominioIndContagem.DI, true,  mapBuscarConsumosRelacionadoInternacoesDeDetermindadoProcessamento, mapBuscarConsumosRelacionadoOcv, mapBuscarConsumosContendoMaterial, listaInternacoes, listaConsumoPorInternacao, listaPhisPorInternacao, listaMateriaisPorInternacao, pacientesFaturamentoPendenteParaAtualizar, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, pAghuSigCodConvParticular, pAghuSigConvTipoTabPgtoCodMedic, pAghuSigConvTipoItemCodMedicDialise, pConvenioSus,listaPAghuSigConvTipoTabPgtoListaCodExames, listaPAghuSigConvTipoItemListaCodExames, pAghuSigConvTabPgtoTpInsumoDietas, pAghuSigConvTipoItemGrupoOrtProtCod, pAghuSigConvTipoTabPgtoCodMedicQuimio);
		this.executarAssociacao(DominioIndContagem.DM, true,  mapBuscarConsumosRelacionadoInternacoesDeDetermindadoProcessamento, mapBuscarConsumosRelacionadoOcv, mapBuscarConsumosContendoMaterial, listaInternacoes, listaConsumoPorInternacao, listaPhisPorInternacao, listaMateriaisPorInternacao, pacientesFaturamentoPendenteParaAtualizar, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, pAghuSigCodConvParticular, pAghuSigConvTipoTabPgtoCodMedic, pAghuSigConvTipoItemCodMedicDialise, pConvenioSus,listaPAghuSigConvTipoTabPgtoListaCodExames, listaPAghuSigConvTipoItemListaCodExames, pAghuSigConvTabPgtoTpInsumoDietas, pAghuSigConvTipoItemGrupoOrtProtCod, pAghuSigConvTipoTabPgtoCodMedicQuimio);
		this.executarAssociacao(DominioIndContagem.OP, false, mapBuscarConsumosRelacionadoInternacoesDeDetermindadoProcessamento, mapBuscarConsumosRelacionadoOcv, mapBuscarConsumosContendoMaterial, listaInternacoes, listaConsumoPorInternacao, listaPhisPorInternacao, listaMateriaisPorInternacao, pacientesFaturamentoPendenteParaAtualizar, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, pAghuSigCodConvParticular, pAghuSigConvTipoTabPgtoCodMedic, pAghuSigConvTipoItemCodMedicDialise, pConvenioSus,listaPAghuSigConvTipoTabPgtoListaCodExames, listaPAghuSigConvTipoItemListaCodExames, pAghuSigConvTabPgtoTpInsumoDietas, pAghuSigConvTipoItemGrupoOrtProtCod, pAghuSigConvTipoTabPgtoCodMedicQuimio);
		this.executarAssociacao(DominioIndContagem.QM, false, mapBuscarConsumosRelacionadoInternacoesDeDetermindadoProcessamento, mapBuscarConsumosRelacionadoOcv, mapBuscarConsumosContendoMaterial, listaInternacoes, listaConsumoPorInternacao, listaPhisPorInternacao, listaMateriaisPorInternacao, pacientesFaturamentoPendenteParaAtualizar, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, pAghuSigCodConvParticular, pAghuSigConvTipoTabPgtoCodMedic, pAghuSigConvTipoItemCodMedicDialise, pConvenioSus,listaPAghuSigConvTipoTabPgtoListaCodExames, listaPAghuSigConvTipoItemListaCodExames, pAghuSigConvTabPgtoTpInsumoDietas, pAghuSigConvTipoItemGrupoOrtProtCod, pAghuSigConvTipoTabPgtoCodMedicQuimio);
		
		
		for (Integer cacSeq : pacientesFaturamentoPendenteParaAtualizar) {
			SigCalculoAtdPaciente paciente = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().obterPorChavePrimaria(cacSeq);
			paciente.setIndFatPendente(false);
			this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().atualizar(paciente);
		}
		this.commitProcessamentoCusto();
		
		//Distribuir valor que falta dos convênios na internação
		SigCategoriaConsumos categoriaOutrasReceitas =  this.getProcessamentoCustoUtils().getSigCategoriaConsumosDAO().obterCategoriaConsumoPorIndicadorContagem(DominioIndContagem.OR);
		if(categoriaOutrasReceitas == null){
			throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_BUSCA_CATEGORIA_CONSUMO);
		}
		
		List<InternacaoConvenioVO> listaInternacaoesConvenio = this.getProcessamentoCustoUtils().getSigCalculoAtdReceitaDAO().pesquisarIternacoesConvenioSus(sigProcessamentoCusto.getSeq(), pConvenioSus, false);
		for (InternacaoConvenioVO vo : listaInternacaoesConvenio) {
			
			BigDecimal valorTotalReceitaInternacao = this.getProcessamentoCustoUtils().getSigCalculoAtdReceitaDAO().obterValorTotalReceitaInternacao(sigProcessamentoCusto.getSeq(), pConvenioSus, vo.getIntSeq(),false);
			BigDecimal valorTotalContaConvenio = this.getProcessamentoCustoUtils().getSigCalculoAtdReceitaDAO().obterValorTotalContaConvenio(vo.getIntSeq());
			
			SigCalculoAtdReceita receita = new SigCalculoAtdReceita();
			receita.setCriadoEm(new Date());
			receita.setCppSeq(vo.getCppSeq());
			receita.setQtde(BigDecimal.ZERO);
			receita.setRapServidores(rapServidores);
			receita.setVlrReceita(valorTotalContaConvenio.subtract(valorTotalReceitaInternacao));
			receita.setCtcSeq(categoriaOutrasReceitas.getSeq());
			this.getProcessamentoCustoUtils().getSigCalculoAtdReceitaDAO().persistir(receita);
		}
		this.commitProcessamentoCusto();
	}
	
	private void executarAssociacao( DominioIndContagem indContagem, Boolean executarRn01, Map<Integer, List<SigConsumosInternacoesVO>> mapBuscarConsumosRelacionadoInternacoesDeDetermindadoProcessamento, Map<Integer, List<SigConsumosInternacoesVO>> mapBuscarConsumosRelacionadoOcv, Map<Integer, List<SigConsumosInternacoesVO>> mapObterConsumosContendoMaterial, Set<Integer> listaInternacoes, Map<Integer, List<SigConsumosInternacoesVO>> listaConsumoPorInternacao,  Map<Integer, Set<Integer>> listaPhisPorInternacao, Map<Integer, Set<Integer>> listaMateriaisPorInternacao, Set<Integer> pacientesFaturamentoPendenteParaAtualizar, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, Short pAghuSigCodConvParticular, Short pAghuSigConvTipoTabPgtoCodMedic, Byte pAghuSigConvTipoItemCodMedicDialise, Short pConvenioSus, List<Short> listaPAghuSigConvTipoTabPgtoListaCodExames, List<Byte> listaPAghuSigConvTipoItemListaCodExames, Short pAghuSigConvTabPgtoTpInsumoDietas, Short pAghuSigConvTipoItemGrupoOrtProtCod, Short pAghuSigConvTipoTabPgtoCodMedicQuimio) throws ApplicationBusinessException {
		
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_INICIO_ASSOCIACAO_CUSTOS_PAC_PHI");
		
		SigCategoriaConsumos categoriaConsumo = this.getProcessamentoCustoUtils().getSigCategoriaConsumosDAO().obterCategoriaConsumoPorIndicadorContagem(indContagem);

		if(executarRn01){
			// #32240 - RN01
			List<SigConsumosInternacoesVO> listaCalculoAtdConsumo = mapBuscarConsumosRelacionadoInternacoesDeDetermindadoProcessamento.get(categoriaConsumo.getSeq());
			this.agruparDadosPorInternacao(listaCalculoAtdConsumo, listaInternacoes, listaConsumoPorInternacao, listaPhisPorInternacao, listaMateriaisPorInternacao);
			
			// #47286 - RN01
			List<SigConsumosInternacoesVO> listaConsumosOcv = mapBuscarConsumosRelacionadoOcv.get(categoriaConsumo.getSeq());
			this.agruparDadosPorInternacao(listaConsumosOcv, listaInternacoes, listaConsumoPorInternacao, listaPhisPorInternacao, listaMateriaisPorInternacao);
			
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_SUCESSO_ASSOCIACAO_CUSTOS_PAC_PHI");
		}
		else{
			// # 47286 RN02
			List<SigConsumosInternacoesVO> listaConsumosMaterial = mapObterConsumosContendoMaterial.get(categoriaConsumo.getSeq());
			this.agruparDadosPorInternacao(listaConsumosMaterial, listaInternacoes, listaConsumoPorInternacao, listaPhisPorInternacao, listaMateriaisPorInternacao);
		}
		
		for(Integer intSeq : listaInternacoes){
			
			this.gravarReceita(intSeq, listaPhisPorInternacao.get(intSeq), listaMateriaisPorInternacao.get(intSeq),  listaConsumoPorInternacao.get(intSeq), pacientesFaturamentoPendenteParaAtualizar, rapServidores, indContagem, pAghuSigCodConvParticular, pAghuSigConvTipoTabPgtoCodMedic, pAghuSigConvTipoItemCodMedicDialise, listaPAghuSigConvTipoTabPgtoListaCodExames, listaPAghuSigConvTipoItemListaCodExames, pAghuSigConvTabPgtoTpInsumoDietas, pAghuSigConvTipoItemGrupoOrtProtCod, pAghuSigConvTipoTabPgtoCodMedicQuimio);
		}
		
		listaInternacoes.clear();
		listaConsumoPorInternacao.clear();
		listaPhisPorInternacao.clear();
		listaMateriaisPorInternacao.clear();
	}
	
	private void agruparDadosPorInternacao(List<SigConsumosInternacoesVO> listaCalculoAtdConsumo, Set<Integer> listaInternacoes, Map<Integer, List<SigConsumosInternacoesVO>> listaConsumoPorInternacao, Map<Integer, Set<Integer>> listaPhisPorInternacao, Map<Integer, Set<Integer>> listaMateriaisPorInternacao) {
		
		if(ProcessamentoCustoUtils.verificarListaNaoVazia(listaCalculoAtdConsumo)){
			
			for(SigConsumosInternacoesVO calculo : listaCalculoAtdConsumo) {
				
				if(!listaInternacoes.contains(calculo.getIntSeq())){
					listaInternacoes.add(calculo.getIntSeq());
					listaPhisPorInternacao.put(calculo.getIntSeq(), new HashSet<Integer>());
					listaMateriaisPorInternacao.put(calculo.getIntSeq(), new HashSet<Integer>());
					listaConsumoPorInternacao.put(calculo.getIntSeq(), new ArrayList<SigConsumosInternacoesVO>());
				}
				
				listaConsumoPorInternacao.get(calculo.getIntSeq()).add(calculo);
				
				if(calculo.getPhiSeq() != null){
					listaPhisPorInternacao.get(calculo.getIntSeq()).add(calculo.getPhiSeq());
				}
				
				if(calculo.getMatCodigo() != null){
					listaMateriaisPorInternacao.get(calculo.getIntSeq()).add(calculo.getMatCodigo());
				}
			}
		}
	}
	
	private void gravarReceita(Integer intSeq, Set<Integer> listaPhis, Set<Integer> listaMateriais, List<SigConsumosInternacoesVO> listaCpp, Set<Integer> pacientesFaturamentoPendenteParaAtualizar, RapServidores rapServidores, DominioIndContagem indContagem, Short pAghuSigCodConvParticular, Short pAghuSigConvTipoTabPgtoCodMedic, Byte pAghuSigConvTipoItemCodMedicDialise, List<Short> listaPAghuSigConvTipoTabPgtoListaCodExames, List<Byte> listaPAghuSigConvTipoItemListaCodExames, Short pAghuSigConvTabPgtoTpInsumoDietas, Short pAghuSigConvTipoItemGrupoOrtProtCod, Short pAghuSigConvTipoTabPgtoCodMedicQuimio) throws ApplicationBusinessException {
		
		List<SigProcedimentoMedicamentoExameVO> lista = null;
		switch(indContagem){
		case MD: 
			lista = this.getProcessamentoCustoUtils().getProcEfetDAO().buscarProcedimentosComPhiMedicamentosPorInternacao(intSeq, pAghuSigCodConvParticular, pAghuSigConvTipoTabPgtoCodMedic, listaPhis, pAghuSigConvTipoItemCodMedicDialise);
			break;
		case EX:
			lista = this.getProcessamentoCustoUtils().getProcEfetDAO().buscarProcedimentosComPhiExamesPorInternacao(intSeq, pAghuSigCodConvParticular,listaPAghuSigConvTipoTabPgtoListaCodExames, listaPAghuSigConvTipoItemListaCodExames, listaPhis);
			break;
		case CE: 
			lista = this.getProcessamentoCustoUtils().getProcEfetDAO().buscarProcedimentosComPhiCuidadosEnfermagemPorInternacao(intSeq, listaPhis);
			break;
		case DI:
			lista = this.getProcessamentoCustoUtils().getProcEfetDAO().buscarProcedimentosComPhiDietasPorInternacao(intSeq, listaPhis, pAghuSigConvTipoTabPgtoCodMedic, pAghuSigCodConvParticular, pAghuSigConvTabPgtoTpInsumoDietas);
			break;
		case DM: 
			lista = this.getProcessamentoCustoUtils().getProcEfetDAO().buscarProcedimentosComPhiMedicamentosDialisePorInternacao(intSeq, listaPhis, pAghuSigConvTipoTabPgtoCodMedic,pAghuSigCodConvParticular, pAghuSigConvTipoItemCodMedicDialise);
			break;
		case OP:
			lista = this.getProcessamentoCustoUtils().getProcEfetDAO().buscarReceitaParaOrteseEProtese(intSeq, listaMateriais, pAghuSigConvTipoItemGrupoOrtProtCod, pAghuSigCodConvParticular);
			break;
		case QM:
			lista = this.getProcessamentoCustoUtils().getProcEfetDAO().buscarReceitaParaQuimioterapia(intSeq, listaMateriais, pAghuSigCodConvParticular, pAghuSigConvTipoTabPgtoCodMedicQuimio, pAghuSigConvTipoTabPgtoCodMedic);
			break;
		default:
			lista = new ArrayList<SigProcedimentoMedicamentoExameVO>();
			break;
		}
		this.persistirConsumos(rapServidores, listaCpp, lista, pacientesFaturamentoPendenteParaAtualizar);
	}
	
	private void persistirConsumos(RapServidores rapServidores, List<SigConsumosInternacoesVO> listaCpp, List<SigProcedimentoMedicamentoExameVO> listaProcedimentos, Set<Integer> pacientesFaturamentoPendenteParaAtualizar) throws ApplicationBusinessException {
		
		BigDecimal valorReceita;
		for(SigProcedimentoMedicamentoExameVO ex : listaProcedimentos) {
			
			for(SigConsumosInternacoesVO obj : listaCpp) {
				
				//Quando for nulo, então está inserindo pelo material e deve inserir todos da internação
				//Caso contrário tem que comparar o phi, para inserir somente quando for o phi atual do laço
				if(ex.getPhiSeq() == null || ex.getPhiSeq().equals(obj.getPhiSeq())){
					
					if(obj.getIndFatPendente() != null && obj.getIndFatPendente() == Boolean.TRUE){
						pacientesFaturamentoPendenteParaAtualizar.add(obj.getCacSeq());
					}
					
					if(obj.getCctCodigo() != null) {
						if(ex.getQtde().equals(BigDecimal.ZERO)){//Evitar divisão por zero que pode ocorrer
							continue;//Não deve inserir essa receita
						}
						else{
							valorReceita = (ex.getQtdeCsh().divide(ex.getQtde(), RoundingMode.HALF_EVEN)).multiply(obj.getQtde());
						}
					} else {
						valorReceita = ex.getQtdeCsh();
					}
					SigCalculoAtdReceita receita = new SigCalculoAtdReceita();
					receita.setCriadoEm(new Date());
					receita.setCppSeq(obj.getCppSeq());
					
					if(obj.getOcvSeq()!= null){
						receita.setOcvSeq(obj.getOcvSeq());
					}
					else{
						receita.setPhiSeq(ex.getPhiSeq());
					}
					
					receita.setQtde(obj.getQtde());
					receita.setRapServidores(rapServidores);
					receita.setVlrReceita(valorReceita);
					receita.setCctCodigo(obj.getCcsCctCodigo());
					receita.setCtcSeq(obj.getCtcSeq());
					this.getProcessamentoCustoUtils().getSigCalculoAtdReceitaDAO().persistir(receita);
				}
			}
			this.commitProcessamentoCusto();
		}
	}
	
	private BigDecimal buscarParametroValorNumerico(AghuParametrosEnum parametro, String msgErro, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, SigProcessamentoCusto sigProcessamentoCusto) throws ApplicationBusinessException{
		try {
			return this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(parametro).getVlrNumerico();
		} catch (ApplicationBusinessException e) {
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores,sigProcessamentoPassos, DominioEtapaProcessamento.G, msgErro);
			return BigDecimal.ZERO;
		}
	}
	
	private String buscarParametroValorTexto(AghuParametrosEnum parametro, String msgErro, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, SigProcessamentoCusto sigProcessamentoCusto) throws ApplicationBusinessException{
		try {
			return this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(parametro).getVlrTexto();
		} catch (ApplicationBusinessException e) {
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores,sigProcessamentoPassos, DominioEtapaProcessamento.G, msgErro);
			return "";
		}
	}
}
