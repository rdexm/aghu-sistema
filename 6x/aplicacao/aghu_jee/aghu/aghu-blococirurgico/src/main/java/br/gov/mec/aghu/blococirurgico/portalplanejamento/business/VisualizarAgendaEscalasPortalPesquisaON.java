package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasAgendaEscalaDiaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasAgendaEscalaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasParametrosVO;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.util.CapitalizeEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * 
 * 
 */
@Stateless
public class VisualizarAgendaEscalasPortalPesquisaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(VisualizarAgendaEscalasPortalPesquisaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcProcedimentoCirurgicoDAO mbcProcedimentoCirurgicoDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	private MbcProfAtuaUnidCirgsDAO mbcProfAtuaUnidCirgsDAO;


	@EJB
	private IAmbulatorioFacade iAmbulatorioFacade;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;

	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade iBlocoCirurgicoPortalPlanejamentoFacade;

	/**
	 * Classe responsável por prover os metodos para a tela de pesquisa de cirurgias aba Agenda e Escala
	 */
	private static final long serialVersionUID = -5632979831718599749L;
	
	public PortalPesquisaCirurgiasAgendaEscalaDiaVO pesquisarAgendasEscalasDia(PortalPesquisaCirurgiasParametrosVO parametros, Date data, Boolean reverse) throws ApplicationBusinessException{
		PortalPesquisaCirurgiasAgendaEscalaDiaVO agendasEscalas = new PortalPesquisaCirurgiasAgendaEscalaDiaVO();
		
		if(parametros.getDataInicio() != null){
			agendasEscalas = buscarAgendasEscalasPorDataInicio(parametros, data, reverse);
		} else if(parametros.getPacCodigo() != null){
			agendasEscalas = buscarAgendasEscalasPorPaciente(parametros,data, reverse);
		}
		
		return agendasEscalas;
	}
	
	private List<PortalPesquisaCirurgiasAgendaEscalaVO> buscarAgendasEscalaCirurgiasUnion1(PortalPesquisaCirurgiasParametrosVO parametros, Date diaAtual, Boolean reverse){
		List<PortalPesquisaCirurgiasAgendaEscalaVO> agendas = getMbcProfCirurgiasDAO().pesquisarAgendasEscalaCirurgiasUnion1(parametros, diaAtual, reverse);
		List<PortalPesquisaCirurgiasAgendaEscalaVO> lista = new ArrayList<PortalPesquisaCirurgiasAgendaEscalaVO>();
		
		for(PortalPesquisaCirurgiasAgendaEscalaVO item : agendas){
			Long retorno = getBlocoCirurgicoFacade().verificarSeEscalaPortalAgendamentoTemCirurgia(item.getAgdSeq(), item.getData());
			if((item.getSituacaoAgenda().equals(DominioSituacaoAgendas.AG) || 
					item.getSituacaoAgenda().equals(DominioSituacaoAgendas.LE)) ||
					(item.getSituacaoAgenda().equals(DominioSituacaoAgendas.ES) && retorno == 0)){
				item.setOrdemVisualizacao(4);
				lista.add(item);
			}
		}

		return lista;
	}
	
	private List<PortalPesquisaCirurgiasAgendaEscalaVO> buscarAgendasEscalaCirurgiasUnion2(PortalPesquisaCirurgiasParametrosVO parametros, Date diaAtual, Boolean reverse) throws ApplicationBusinessException{
		AghParametros motivoDesmarcar = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MOT_DESMARCAR);
		AghParametros motivoDesmarcarAdm = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MOT_DESMARCAR_ADM);
		
		List<PortalPesquisaCirurgiasAgendaEscalaVO> escalas = getMbcProfCirurgiasDAO().pesquisarAgendasEscalaCirurgiasUnion2(parametros, 
				diaAtual, new Short[]{Short.valueOf(motivoDesmarcar.getVlrNumerico().toString()), Short.valueOf(motivoDesmarcarAdm.getVlrNumerico().toString())}, reverse);
		List<PortalPesquisaCirurgiasAgendaEscalaVO> lista = new ArrayList<PortalPesquisaCirurgiasAgendaEscalaVO>();
		MbcProcedimentoCirurgicos procedimento = null;
		
		for(PortalPesquisaCirurgiasAgendaEscalaVO item : escalas){
			if(item.getCrgSeq() != null && parametros.getPciSeqPortal() != null){ 
				procedimento = getMbcProcedimentoCirurgicoDAO().obterPorCirurgiaIndPrincipalSituacaoIndRespProcJoinComMbcProcEspPorCirurgias(item.getCrgSeq(), 
					true, DominioSituacao.A, null);
			}
			
			if(parametros.getPciSeqPortal() == null || (procedimento != null && procedimento.getSeq().equals(parametros.getPciSeqPortal()))){
				if(item.getSituacaoCirurgia().equals(DominioSituacaoCirurgia.RZDA)){
					item.setOrdemVisualizacao(1);
				} else if(item.getSituacaoCirurgia().equals(DominioSituacaoCirurgia.AGND) ||
						item.getSituacaoCirurgia().equals(DominioSituacaoCirurgia.CHAM) ||
						item.getSituacaoCirurgia().equals(DominioSituacaoCirurgia.PREP) ||
						item.getSituacaoCirurgia().equals(DominioSituacaoCirurgia.TRAN)){
					item.setOrdemVisualizacao(3);
				} else if(item.getSituacaoCirurgia().equals(DominioSituacaoCirurgia.CANC)){
					item.setOrdemVisualizacao(5);
				} else {
					item.setOrdemVisualizacao(6);
				}
				lista.add(item);
			}
		}

		return lista;
	}

	private List<PortalPesquisaCirurgiasAgendaEscalaVO> buscarAgendaEscalas(PortalPesquisaCirurgiasParametrosVO parametros, Date diaAtual, Boolean reverse) throws ApplicationBusinessException{
		List<PortalPesquisaCirurgiasAgendaEscalaVO> lista = new ArrayList<PortalPesquisaCirurgiasAgendaEscalaVO>();
		lista.addAll(buscarAgendasEscalaCirurgiasUnion1(parametros, diaAtual, reverse));
		lista.addAll(buscarAgendasEscalaCirurgiasUnion2(parametros, diaAtual, reverse));
		
		Collections.sort(lista, new Comparator<PortalPesquisaCirurgiasAgendaEscalaVO>(){
			@Override
			public int compare(PortalPesquisaCirurgiasAgendaEscalaVO o1, PortalPesquisaCirurgiasAgendaEscalaVO o2) {
				int retorno = o1.getData().compareTo(o2.getData());
				
				if(retorno == 0){
					retorno = o1.getOrdemVisualizacao().compareTo(o2.getOrdemVisualizacao());
				}
								
				if(retorno == 0){
					if(o1.getDthrPrevInicio() == null && o2.getDthrPrevInicio() != null){
						retorno = 1;
					} else if(o1.getDthrPrevInicio() != null && o2.getDthrPrevInicio() == null){
						retorno = -1;
					} else if(o1.getDthrPrevInicio() != null && o2.getDthrPrevInicio() != null){
						retorno = o1.getDthrPrevInicio().compareTo(o2.getDthrPrevInicio());
					}
				} 
				
				if(retorno == 0){
					if(o1.getAgdSeq() == null && o2.getAgdSeq() != null){
						retorno = 1;
					} else if(o1.getAgdSeq() != null && o2.getAgdSeq() == null){
						retorno = -1;
					} else if(o1.getAgdSeq() != null && o2.getAgdSeq() != null){
						retorno = o1.getAgdSeq().compareTo(o2.getAgdSeq());
					}
				}
				return retorno;
			}
		});
		
		return lista;
	}
	
	private PortalPesquisaCirurgiasAgendaEscalaDiaVO buscarAgendasEscalasPorPaciente(PortalPesquisaCirurgiasParametrosVO parametros, Date data,
			Boolean reverse) throws ApplicationBusinessException{
		List<Date> datasValidas = buscarDatasPaciente(parametros,data,reverse);
		PortalPesquisaCirurgiasAgendaEscalaDiaVO agendasEscalas = new PortalPesquisaCirurgiasAgendaEscalaDiaVO();
		int count = 0;
		for(Date dataAtual : datasValidas){
			List<PortalPesquisaCirurgiasAgendaEscalaVO> lista = buscarAgendaEscalas(parametros, dataAtual, false);
				
			agendasEscalas.getDataAgendaDate()[count] = dataAtual;
			
			for(PortalPesquisaCirurgiasAgendaEscalaVO item : lista){
				formatarCamposVO(item);
				
				if(count == 0 && item.getData().equals(dataAtual)){
					agendasEscalas.getPrimeiroDia().add(item);
				} else if(count == 1 && item.getData().equals(dataAtual)){
					agendasEscalas.getSegundoDia().add(item);
				} else if(count == 2 && item.getData().equals(dataAtual)){
					agendasEscalas.getTerceiroDia().add(item);
				}
			}
			count++;
		}
		return agendasEscalas;
	}

	private PortalPesquisaCirurgiasAgendaEscalaDiaVO buscarAgendasEscalasPorDataInicio(PortalPesquisaCirurgiasParametrosVO parametros, Date data, Boolean reverse) throws ApplicationBusinessException{
		PortalPesquisaCirurgiasAgendaEscalaDiaVO agendasEscalas = new PortalPesquisaCirurgiasAgendaEscalaDiaVO();
		Date dataAtual = null, anterior = null;
		int count = 0;
		List<PortalPesquisaCirurgiasAgendaEscalaVO> listaAgendas = buscarAgendaEscalas(parametros, 
				(data != null) ? data : parametros.getDataInicio(), reverse);
		
		if(!listaAgendas.isEmpty()){
			dataAtual = listaAgendas.get(0).getData();
			//se for paginacao para tras inverte a lista para buscar pelas datas válidas (faz busca sempre para a frente)
			if(reverse){
				Collections.reverse(listaAgendas);
				dataAtual = listaAgendas.get(0).getData();
			}
		}
		
		for(PortalPesquisaCirurgiasAgendaEscalaVO item : listaAgendas){
			formatarCamposVO(item);
			if(!DateUtil.isDatasIguais(dataAtual, item.getData())){
				agendasEscalas.getDataAgendaDate()[count] = dataAtual;
				if(count+1 >= 3){
					break;
				}else {
					dataAtual = item.getData();
					count++;
				}
			}			
			if(count == 0){
				agendasEscalas.getPrimeiroDia().add(item);
			} else if(count == 1){
				agendasEscalas.getSegundoDia().add(item);
			} else if(count == 2){
				agendasEscalas.getTerceiroDia().add(item);
			} 
			anterior = dataAtual;
		}

		agendasEscalas.getDataAgendaDate()[count] = anterior;

		//se for paginacao para tras inverte as listas para ficar em ordem
		if(reverse){
			inverterListas(agendasEscalas);
		}
		
		return agendasEscalas;
	}
	
	//RN2
	public PortalPesquisaCirurgiasAgendaEscalaVO formatarCamposVO(PortalPesquisaCirurgiasAgendaEscalaVO item){
			
		item.setNomePaciente(getBlocoCirurgicoPortalPlanejamentoFacade().obterNomeIntermediarioPacienteAbreviado(
				getAmbulatorioFacade().obterDescricaoCidCapitalizada(item.getNomePaciente(), CapitalizeEnum.TODAS)));
		item.setProntuarioFormatado(CoreUtil.formataProntuario(item.getProntuario()));
		item.setDescricaoUnidade(getAmbulatorioFacade().obterDescricaoCidCapitalizada(item.getDescricaoUnidade(), CapitalizeEnum.TODAS));
		
		item.setEspecialidade(getAmbulatorioFacade().obterDescricaoCidCapitalizada(item.getEspecialidade(), CapitalizeEnum.TODAS));
		item.setConvenio(getAmbulatorioFacade().obterDescricaoCidCapitalizada(item.getConvenio(), CapitalizeEnum.TODAS));
		
		RapServidores servidor = getRegistroColaboradorFacade().obterServidor(new RapServidoresId(item.getPucSerMatricula(), item.getPucSerVinCodigo()));
		item.setEquipe(getAmbulatorioFacade().obterDescricaoCidCapitalizada(servidor.getPessoaFisica().getNomeUsualOuNome(), CapitalizeEnum.TODAS));
		
		if(item.getProcedimento() == null && item.getCrgSeq() != null){
			item.setProcedimento(getBlocoCirurgicoPortalPlanejamentoFacade().pesquisarProcEspCirurgico(item.getCrgSeq()));
		}
		
		item.setProcedimento(getAmbulatorioFacade().obterDescricaoCidCapitalizada(item.getProcedimento(), CapitalizeEnum.TODAS));
		
		if((item.getNaturezaAgendamento() != null && item.getNaturezaAgendamento().equals(DominioNaturezaFichaAnestesia.APR)) || 
				(item.getUtilizacaoSala() != null && item.getUtilizacaoSala().equals(DominioNaturezaFichaAnestesia.APR))){
			item.setAproveitamentoSala(" - Aproveitamento");
		}
		
		colorirAgendaEscalas(item);
		
		return item;
	}
	
	//RN3
	public void colorirAgendaEscalas(PortalPesquisaCirurgiasAgendaEscalaVO item){
		if(item.getSituacaoCirurgia() != null){
			if(item.getSituacaoCirurgia().equals(DominioSituacaoCirurgia.RZDA)){
				item.setRealizado(true);
			} else if(item.getSituacaoCirurgia().equals(DominioSituacaoCirurgia.CANC)){
				item.setCancelado(true);
			} else if(item.getSituacaoCirurgia().equals(DominioSituacaoCirurgia.AGND) || 
					item.getSituacaoCirurgia().equals(DominioSituacaoCirurgia.CHAM) ||
					item.getSituacaoCirurgia().equals(DominioSituacaoCirurgia.PREP) ||
					item.getSituacaoCirurgia().equals(DominioSituacaoCirurgia.TRAN)){
				item.setEscala(true);
			} else {
				item.setPlanejado(true);
			}
		} else {
			item.setPlanejado(true);
		}
	}
	
	
	private List<Date> buscarDatasPaciente(PortalPesquisaCirurgiasParametrosVO parametros, Date data,
			Boolean reverse) throws ApplicationBusinessException{
		
		List<Date> datasOrdenadas = new ArrayList<Date>();
		
		datasOrdenadas = buscarTodasDatasPaciente(parametros);
		//pegar tres datas posterior ou anterior a data do parametro
		
		if(data!=null){
			if(reverse){
				return retornaDatasOrdenadasRetroceder(datasOrdenadas,DateUtil.adicionaDias(data,1));
			}else{
				return retornaDatasOrdenadasAvancar(datasOrdenadas,DateUtil.adicionaDias(data,-1));
			}
		}else{
			//removendo datas extras
			if (datasOrdenadas.size() > 3){
				for(int i = datasOrdenadas.size()-1; i > 2; i--){
					datasOrdenadas.remove(i);
				}
			}
		}
		return datasOrdenadas;
	}
	
	private List<Date> retornaDatasOrdenadasAvancar(List<Date> datasOrdenadas, Date data){
		List<Date> datasOrdenadasPaciente = new ArrayList<Date>();
		int index =0;
		for (Date dateDatasOrdenadas : datasOrdenadas) {
			if(DateUtil.isDatasIguais(dateDatasOrdenadas,data)){
				index = datasOrdenadas.indexOf(dateDatasOrdenadas)+1;
				break;
			}
		}
		int i =0;
        while((index < datasOrdenadas.size()) && (i<3)){
        	datasOrdenadasPaciente.add(datasOrdenadas.get(index));
        	index++;
      	   	i++;
        }
		
		return datasOrdenadasPaciente;
	}
	
	private List<Date> retornaDatasOrdenadasRetroceder(List<Date> datasOrdenadas, Date data){
		
		List<Date> datasOrdenadasPaciente = new ArrayList<Date>();
		int index =0;
		for (Date dateDatasOrdenadas : datasOrdenadas) {
			if(DateUtil.isDatasIguais(dateDatasOrdenadas,data)){
				index = datasOrdenadas.indexOf(dateDatasOrdenadas)-1;
				break;
			}
		}
		int j=0;
		while(j<3 && index>=0){
        	   datasOrdenadasPaciente.add(datasOrdenadas.get(index));
        	   index--;
        	   j++;
        }
		
		//ordenar
		Set<Date> set = new HashSet<Date>();
		set.addAll(datasOrdenadasPaciente);
		datasOrdenadasPaciente.clear();
		datasOrdenadasPaciente.addAll(set);
		Collections.sort(datasOrdenadasPaciente);
		
		return datasOrdenadasPaciente;
	}
	
	//implementacao da C1
	public List<Date> buscarTodasDatasPaciente(PortalPesquisaCirurgiasParametrosVO parametros) throws ApplicationBusinessException{
		
		List<Date> datasOrdenadas = new ArrayList<Date>();
		List<Date> datasAgendas = new ArrayList<Date>();
		List<MbcAgendas> agendas = getMbcAgendasDAO().buscarDatasAgendaEscala(parametros);
		for (MbcAgendas mbcAgendas : agendas) {
			if(mbcAgendas.getIndSituacao().equals(DominioSituacaoAgendas.AG) || mbcAgendas.getIndSituacao().equals(DominioSituacaoAgendas.LE)){
				datasAgendas.add(mbcAgendas.getDtAgenda());
			}else{
				List<MbcCirurgias> cirurgiasAgendamentoSemMotivo = getMbcCirurgiasDAO().buscarCirurgiaPorAgendamentoSemMotivoCancelamento(mbcAgendas.getSeq(), mbcAgendas.getDtAgenda());
				if(mbcAgendas.getIndSituacao().equals(DominioSituacaoAgendas.ES) && cirurgiasAgendamentoSemMotivo.isEmpty() ) {
					datasAgendas.add(mbcAgendas.getDtAgenda());
				}
			}
		}
		AghParametros motivoDesmarcar = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MOT_DESMARCAR);
		AghParametros motivoDesmarcarAdm = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MOT_DESMARCAR_ADM);

		List<Date> datas = getMbcProfCirurgiasDAO().buscarDatasAgendaEscala(parametros, new Short[]{Short.valueOf(motivoDesmarcar.getVlrNumerico().toString()), Short.valueOf(motivoDesmarcarAdm.getVlrNumerico().toString())});
		datasOrdenadas.addAll(datasAgendas);
		datasOrdenadas.addAll(datas);
		
		Set<Date> set = new HashSet<Date>();
		set.addAll(datasOrdenadas);
		datasOrdenadas.clear();
		datasOrdenadas.addAll(set);
		Collections.sort(datasOrdenadas);
		
		return datasOrdenadas;
	}
	
	private void inverterListas(PortalPesquisaCirurgiasAgendaEscalaDiaVO escalaDias){
		List<PortalPesquisaCirurgiasAgendaEscalaVO> listaAuxiliar = escalaDias.getPrimeiroDia();
		escalaDias.setPrimeiroDia(escalaDias.getTerceiroDia());
		escalaDias.setTerceiroDia(listaAuxiliar);
		Date dataAuxiliar = escalaDias.getDataAgendaDate()[0];
		escalaDias.getDataAgendaDate()[0] = escalaDias.getDataAgendaDate()[2];
		escalaDias.getDataAgendaDate()[2] = dataAuxiliar;
		
		Collections.reverse(escalaDias.getPrimeiroDia());
		Collections.reverse(escalaDias.getSegundoDia());
		Collections.reverse(escalaDias.getTerceiroDia());
	}
	
	protected MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO(){
		return mbcProfCirurgiasDAO;
	}
	
	protected MbcProfAtuaUnidCirgsDAO getMbcProfAtuaUnidCirgsDAO(){
		return mbcProfAtuaUnidCirgsDAO;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade(){
		return this.iBlocoCirurgicoFacade;
	}
	
	protected IBlocoCirurgicoPortalPlanejamentoFacade getBlocoCirurgicoPortalPlanejamentoFacade(){
		return this.iBlocoCirurgicoPortalPlanejamentoFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.iAmbulatorioFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}
	
	protected MbcProcedimentoCirurgicoDAO getMbcProcedimentoCirurgicoDAO(){
		return mbcProcedimentoCirurgicoDAO;
	}
}
