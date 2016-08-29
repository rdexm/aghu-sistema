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
import br.gov.mec.aghu.dominio.DominioModoCobranca;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
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
import br.gov.mec.aghu.sig.custos.vo.AssociacaoProcedimentoVO;
import br.gov.mec.aghu.sig.custos.vo.InternacaoConvenioVO;
import br.gov.mec.aghu.sig.custos.vo.SigConsumosInternacoesVO;
import br.gov.mec.aghu.sig.custos.vo.SigValorReceitaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * #32238 - Associar valores de receita SUS com os custos do paciente
 * 
 * @author fpalma
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalReceitaSusCustosPacienteRN.class)
public class ProcessamentoCustosMensalReceitaSusCustosPacienteRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = 8151798416631838353L;

	@Override
	public String getTitulo() {
		return "Associar valores de receita SUS com os custos do paciente";
	}

	@Override
	public String getNome() {
		return "ProcessamentoCustosMensalReceitaSusCustosPacienteRN";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 40;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		
        Short pConvenioSus = null;
        try {
            pConvenioSus = this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS).getVlrNumerico().shortValue();
        } catch (ApplicationBusinessException e) {
            this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores,sigProcessamentoPassos, DominioEtapaProcessamento.G, "PARAMETRO_NAO_CADASTRADO_P_CONVENIO_SUS");
        }
		
        this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_INICIO_ASSOCIACAO_CUSTOS_PAC_PHI");
		
        Set<Integer> listaInternacoes = new HashSet<Integer>();
		Map<Integer, List<SigConsumosInternacoesVO>> listaConsumoPorInternacao = new HashMap<Integer, List<SigConsumosInternacoesVO>>();
		Map<Integer, Set<Integer>> listaPhisPorInternacao = new HashMap<Integer, Set<Integer>>();
		SigCategoriaConsumos categoriaConsumo;
		List<SigConsumosInternacoesVO> consumosC1;
		List<SigConsumosInternacoesVO> consumosC2;
		
		//Consulta por todos os registro já agrupados pelas categorias de consumo (mostrou que demora menos tempo do que buscar categoria por categoria) 
		Map<Integer, List<SigConsumosInternacoesVO>> mapObterConsumosRelacionadoInternacoesDeDetermindadoProcessamento = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarConsumosRelacionadoInternacoesDeDetermindadoProcessamento(sigProcessamentoCusto.getSeq(), sigProcessamentoCusto.getDataInicio(), pConvenioSus, Boolean.TRUE);
		Map<Integer, List<SigConsumosInternacoesVO>> mapObterConsumosRelacionadoOcv = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarConsumosRelacionadoOcv(sigProcessamentoCusto.getSeq(), sigProcessamentoCusto.getDataInicio(), pConvenioSus, Boolean.TRUE); 
		Set<Integer> pacientesFaturamentoPendenteParaAtualizar = new HashSet<Integer>();
		
		for(DominioIndContagem indContagem : DominioIndContagem.values()) {
			
			categoriaConsumo = this.getProcessamentoCustoUtils().getSigCategoriaConsumosDAO().obterCategoriaConsumoPorIndicadorContagem(indContagem);
			
			//C1
			consumosC1 = mapObterConsumosRelacionadoInternacoesDeDetermindadoProcessamento.get(categoriaConsumo.getSeq());
			this.agruparDadosPorInternacao(consumosC1, listaInternacoes, listaConsumoPorInternacao, listaPhisPorInternacao);
			
			//C2
			consumosC2 = mapObterConsumosRelacionadoOcv.get(categoriaConsumo.getSeq());
			this.agruparDadosPorInternacao(consumosC2, listaInternacoes, listaConsumoPorInternacao, listaPhisPorInternacao);
			
			for(Integer intSeq : listaInternacoes){
				this.calcularReceitaInternacao(intSeq, listaPhisPorInternacao.get(intSeq), listaConsumoPorInternacao.get(intSeq), pacientesFaturamentoPendenteParaAtualizar, rapServidores);
			}
			
			listaInternacoes.clear();
			listaConsumoPorInternacao.clear();
			listaPhisPorInternacao.clear();
		}
		
		for (Integer cacSeq : pacientesFaturamentoPendenteParaAtualizar) {
			SigCalculoAtdPaciente paciente = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().obterPorChavePrimaria(cacSeq);
			paciente.setIndFatPendente(false);
			this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().atualizar(paciente);
		}
		this.commitProcessamentoCusto();
		
		//Distribuir valor que falta do sus na internação
		SigCategoriaConsumos categoriaOutrasReceitas =  this.getProcessamentoCustoUtils().getSigCategoriaConsumosDAO().obterCategoriaConsumoPorIndicadorContagem(DominioIndContagem.OR);
		if(categoriaOutrasReceitas == null){
			throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_BUSCA_CATEGORIA_CONSUMO);
		}
		
		List<InternacaoConvenioVO> listaInternacaoesConvenio = this.getProcessamentoCustoUtils().getSigCalculoAtdReceitaDAO().pesquisarIternacoesConvenioSus(sigProcessamentoCusto.getSeq(), pConvenioSus, true);
		for (InternacaoConvenioVO vo : listaInternacaoesConvenio) {
			
			BigDecimal valorTotalReceitaInternacao = this.getProcessamentoCustoUtils().getSigCalculoAtdReceitaDAO().obterValorTotalReceitaInternacao(sigProcessamentoCusto.getSeq(), pConvenioSus, vo.getIntSeq(),true);
			BigDecimal valorTotalContaSus = this.getProcessamentoCustoUtils().getSigCalculoAtdReceitaDAO().obterValorTotalContaSus(vo.getIntSeq());
			
			SigCalculoAtdReceita receita = new SigCalculoAtdReceita();
			receita.setCriadoEm(new Date());
			receita.setCppSeq(vo.getCppSeq());
			receita.setQtde(BigDecimal.ZERO);
			receita.setRapServidores(rapServidores);
			receita.setVlrReceita(valorTotalContaSus.subtract(valorTotalReceitaInternacao));
			receita.setCtcSeq(categoriaOutrasReceitas.getSeq());
			this.getProcessamentoCustoUtils().getSigCalculoAtdReceitaDAO().persistir(receita);
		}
		this.commitProcessamentoCusto();
		
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_SUCESSO_ASSOCIACAO_CUSTOS_PAC_PHI");	
	}
	
	private void agruparDadosPorInternacao(List<SigConsumosInternacoesVO> listaCalculoAtdConsumo, Set<Integer> listaInternacoes, Map<Integer, List<SigConsumosInternacoesVO>> listaConsumoPorInternacao, Map<Integer, Set<Integer>> listaPhisPorInternacao) {
		
		if(ProcessamentoCustoUtils.verificarListaNaoVazia(listaCalculoAtdConsumo)){
			
			for(SigConsumosInternacoesVO calculo : listaCalculoAtdConsumo) {
				
				if(!listaInternacoes.contains(calculo.getIntSeq())){
					listaInternacoes.add(calculo.getIntSeq());
					listaPhisPorInternacao.put(calculo.getIntSeq(), new HashSet<Integer>());
					listaConsumoPorInternacao.put(calculo.getIntSeq(), new ArrayList<SigConsumosInternacoesVO>());
				}
				
				listaConsumoPorInternacao.get(calculo.getIntSeq()).add(calculo);
				listaPhisPorInternacao.get(calculo.getIntSeq()).add(calculo.getPhiSeq());
			}
		}
	}
	
	private void calcularReceitaInternacao(Integer intSeq, Set<Integer> listaPhis, List<SigConsumosInternacoesVO> listaCpp, Set<Integer> pacientesFaturamentoPendenteParaAtualizar, RapServidores servidor) throws ApplicationBusinessException {
		
		//C3
		List<SigValorReceitaVO> listaValoresC3 = this.getProcessamentoCustoUtils().getFaturamentoFacade().obterValorEspelhoPelaInternacao(intSeq, DominioSituacaoConta.O, 1);
		
		Set<Long> listaIphAih = new HashSet<Long>();
		List<Integer> listaCthSeq = new ArrayList<Integer>();
		List<SigValorReceitaVO> listaGeral = new ArrayList<SigValorReceitaVO>();
		for(SigValorReceitaVO vo : listaValoresC3) {
			vo.setValorTotal(vo.getValorProcedRealiz().add(vo.getValorShRealiz().add(vo.getValorSpRealiz())));
			listaIphAih.add(vo.getIphCodSusRealiz());
			vo.setQtde(Long.valueOf("1"));
			if(!listaCthSeq.contains(vo.getCthSeq())) {
				listaCthSeq.add(vo.getCthSeq());
			}
			listaGeral.add(vo);
		}
		
		if(ProcessamentoCustoUtils.verificarListaNaoVazia(listaCthSeq)){
			// C4
			List<SigValorReceitaVO> listaValoresC4 = this.getProcessamentoCustoUtils().getFaturamentoFacade().obterValoresReceitaAtosMedicos(listaCthSeq, DominioModoCobranca.V);
			for(SigValorReceitaVO vo : listaValoresC4) {
				vo.setValorTotal(vo.getValorProcedRealiz().add(vo.getValorShRealiz().add(vo.getValorSpRealiz())));
				listaIphAih.add(vo.getIphCodSus());
				listaGeral.add(vo);
			}
			
			if(!listaIphAih.isEmpty()){
				
				// C5
				List<AssociacaoProcedimentoVO> procedimentos = this.getProcessamentoCustoUtils().getFaturamentoFacade().obterValoresProcedimentosAtravesRespectivosCodigosSus(listaPhis, listaIphAih);
				for(AssociacaoProcedimentoVO procedimentoVO : procedimentos) {
					
					BigDecimal valorReceitaTotal = null;
					BigDecimal quantidade = null;
					for(SigValorReceitaVO vo : listaGeral) {
						if(procedimentoVO.getCodTabela().equals(vo.getIphCodSus()) || procedimentoVO.getCodTabela().equals(vo.getIphCodSusRealiz())) {
							valorReceitaTotal = vo.getValorTotal();
							quantidade = BigDecimal.valueOf(vo.getQtde());
							break;
						}
					}
					
					BigDecimal valorReceita;
					SigCalculoAtdReceita receita;
					for(SigConsumosInternacoesVO consumoInternacaoVO : listaCpp) {
						
						if(procedimentoVO.getPhiSeq().equals(consumoInternacaoVO.getPhiSeq())){
							
							//Atualiza o paciente para sem pendencia no faturamento
							if(consumoInternacaoVO.getIndFatPendente() != null && consumoInternacaoVO.getIndFatPendente() == Boolean.TRUE){
								pacientesFaturamentoPendenteParaAtualizar.add(consumoInternacaoVO.getCacSeq());
							}
						
							if(consumoInternacaoVO.getCctCodigo() != null) {
								valorReceita = (valorReceitaTotal.divide(quantidade, RoundingMode.HALF_EVEN)).multiply(consumoInternacaoVO.getQtde());
							} else {
								valorReceita = valorReceitaTotal;
							}
							
							receita = new SigCalculoAtdReceita();
							receita.setCriadoEm(new Date());
							receita.setCppSeq(consumoInternacaoVO.getCppSeq());
							
							if(consumoInternacaoVO.getOcvSeq() != null){
								receita.setOcvSeq(consumoInternacaoVO.getOcvSeq());
							}
							else{
								receita.setPhiSeq(consumoInternacaoVO.getPhiSeq());
							}
							
							receita.setQtde(consumoInternacaoVO.getQtde());
							receita.setRapServidores(servidor);
							receita.setVlrReceita(valorReceita);
							receita.setCctCodigo(consumoInternacaoVO.getCcsCctCodigo());
							receita.setCtcSeq(consumoInternacaoVO.getCtcSeq());
							this.getProcessamentoCustoUtils().getSigCalculoAtdReceitaDAO().persistir(receita);
						}
					}
					this.commitProcessamentoCusto();
				}
			}
		}
	}

}
