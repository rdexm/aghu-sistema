package br.gov.mec.aghu.exames.agendamento.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.exames.agendamento.vo.GradeHorarioExtraVO;
import br.gov.mec.aghu.exames.agendamento.vo.ItemHorarioAgendadoVO;
import br.gov.mec.aghu.exames.agendamento.vo.VAelExaAgendPacVO;
import br.gov.mec.aghu.exames.agendamento.vo.VAelHrGradeDispVO;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelExameHorarioColetaDAO;
import br.gov.mec.aghu.exames.dao.AelGrupoExamesDAO;
import br.gov.mec.aghu.exames.dao.AelHorarioExameDispDAO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.VAelExaAgendPacDAO;
import br.gov.mec.aghu.exames.dao.VAelExameMatAnaliseDAO;
import br.gov.mec.aghu.exames.dao.VAelGradeExameDAO;
import br.gov.mec.aghu.exames.dao.VAelHrGradeDispDAO;
import br.gov.mec.aghu.model.AelExameHorarioColeta;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelHorarioExameDispId;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemHorarioAgendadoId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VAelExaAgendPac;
import br.gov.mec.aghu.model.VAelExaAgendPacId;
import br.gov.mec.aghu.model.VAelGradeExame;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ConsultaHorarioLivreON extends BaseBusiness {


private static final String _HIFEN_ = " - ";

@EJB
private ConsultaHorarioLivreRN consultaHorarioLivreRN;

private static final Log LOG = LogFactory.getLog(ConsultaHorarioLivreON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelGrupoExamesDAO aelGrupoExamesDAO;

@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@Inject
private VAelGradeExameDAO vAelGradeExameDAO;

@Inject
private VAelExaAgendPacDAO vAelExaAgendPacDAO;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@Inject
private AelItemHorarioAgendadoDAO aelItemHorarioAgendadoDAO;

@Inject
private VAelExameMatAnaliseDAO vAelExameMatAnaliseDAO;

@Inject
private AelHorarioExameDispDAO aelHorarioExameDispDAO;

@EJB
private IParametroFacade parametroFacade;

@Inject
private AelExameHorarioColetaDAO aelExameHorarioColetaDAO;

@EJB
private IExamesFacade examesFacade;

@Inject
private VAelHrGradeDispDAO vAelHrGradeDispDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2657082807090290888L;

	public enum ConsultaHorarioLivreONExceptionCode implements BusinessExceptionCode {
		DATA_HORA_MAIOR_QUE_DATA_ATUAL;
	}
	
	/**
	 * método criado para solucionar o problema de PMD regra cyclomatic complexid, do metodo carregarContexto, 
	 * da classe ConsultaHorarioLivre, que chama este método. Naquele método não poderia ser inserido if para 
	 * verificação de paciente!=null, portanto restou esta solução ou refatorar a controler ConsultaHorarioLivre 
	 * dividindo-a em partes. Optou-se por esta solução pois é menos impactante.
	 * 
	 * @param paciente
	 * @param isAmbulatorio
	 * @return
	 */
	public String obterSugestaoAgendamentoPorPaciente(AipPacientes paciente, Boolean isAmbulatorio){
		String retorno =null;
		if(paciente!=null){
			this.obterSugestaoAgendamentoPorPaciente(paciente.getCodigo(), isAmbulatorio);
		}
		return retorno;
	}
	
	public String obterSugestaoAgendamentoPorPaciente(Integer pacCodigo, Boolean isAmbulatorio){
		String retorno =null;
		SimpleDateFormat dataFormatada = null;
		if(isAmbulatorio){
			dataFormatada = new SimpleDateFormat("dd/MM/yyyy");
		} else {
			dataFormatada = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		}
		List<Date> listaDatas = this.getAelItemHorarioAgendadoDAO().pesquisarSugestaoAgendamentoPorPaciente(pacCodigo, isAmbulatorio);
		Set<Date> lista = new LinkedHashSet<Date>();
		if(isAmbulatorio){
			for(Date data: listaDatas){
				data = DateUtil.truncaData(data);
				lista.add(data);
				if(lista.size()==2){
					break;
				}
			}
			listaDatas.clear();
			listaDatas.addAll(lista);	
		}
		
		if(listaDatas!=null && listaDatas.size()==2){
			Date primeiraData = listaDatas.get(0);
			Date segundaData = listaDatas.get(1);
			String primeiraDataFormatada = dataFormatada.format(primeiraData);
			String segundaDataFormatada = dataFormatada.format(segundaData);
			retorno = primeiraDataFormatada+ " e " + segundaDataFormatada;
	
		} else if(listaDatas!=null && listaDatas.size()==1){
			Date primeiraData = listaDatas.get(0);
			String retornoData = dataFormatada.format(primeiraData);
			retorno = retornoData;
		}
		return retorno;
	}
	
	public List<Date> pesquisarSugestaoAgendamentoPorPaciente(Integer pacCodigo, Boolean isAmbulatorio){
		if(isAmbulatorio){
			List<Date> listaDatas = this.getAelItemHorarioAgendadoDAO().pesquisarSugestaoAgendamentoPorPaciente(pacCodigo, isAmbulatorio);
			Set<Date> lista = new LinkedHashSet<Date>();
			if(isAmbulatorio){
				for(Date data: listaDatas){
					data = DateUtil.truncaData(data);
					lista.add(data);
					if(lista.size()==2){
						break;
					}
				}
				listaDatas.clear();
				listaDatas.addAll(lista);	
			}
			return listaDatas;
		} else {
			return this.getAelItemHorarioAgendadoDAO().pesquisarSugestaoAgendamentoPorPaciente(pacCodigo, isAmbulatorio);
		}
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<VAelHrGradeDispVO> pesquisarHorariosLivresParaExame(String sigla, Integer matExame, Short unfExame, Date dataReativacao, Integer soeSeq, Short seqp, Date data, Date hora, Integer grade, AelGrupoExames grupoExame, AelSalasExecutorasExames salaExecutoraExame, RapServidores servidor) throws ApplicationBusinessException, ParseException {
		AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId();
		id.setSeqp(seqp);
		id.setSoeSeq(soeSeq);
		AelItemSolicitacaoExames itemSolicitacaoExame = this.getAelItemSolicitacaoExameDAO().obterPorChavePrimaria(id);
		DominioOrigemAtendimento origemAtendimento = null;
		
		//Popula valores referentes ao tipo de marcacao
		Short unfSeq = null;
		if(itemSolicitacaoExame.getSolicitacaoExame()!=null){
			AelSolicitacaoExames solicitacaoExame = itemSolicitacaoExame.getSolicitacaoExame();
			if(solicitacaoExame.getAtendimento()!=null){
				AghAtendimentos atendimento = solicitacaoExame.getAtendimento();
				if(atendimento!=null){
					origemAtendimento = atendimento.getOrigem();
				}
			}
			if(solicitacaoExame.getUnidadeFuncional()!=null){
				AghUnidadesFuncionais unidadeFuncional = solicitacaoExame.getUnidadeFuncional();
				if(unidadeFuncional!=null){
					unfSeq = unidadeFuncional.getSeq();
				}
			}
		}
		Short tipoMarcacao = this.getConsultaHorarioLivreRN().obterTipoMarcacao(origemAtendimento, unfSeq);
		Short tipoMarcacaoDiv = null;
		AghParametros parametroTipoMarcacao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_MARC_DIV);
		if(parametroTipoMarcacao!=null){
			tipoMarcacaoDiv = parametroTipoMarcacao.getVlrNumerico().shortValue();
		}
		
		
		//Verifica se existe responsavel pela coleta, se existir deve verificar se existe horario de coleta, senao executa query principal
		Boolean verificaColeta = this.getConsultaHorarioLivreRN().verificarResponsavelColetaExame(sigla, matExame);
		
		List<AelExameHorarioColeta> listaExamesHorarioColeta = null;
		List<AelItemSolicitacaoExames> listaItens = null;
		if(verificaColeta){
			listaItens = this.getAelItemSolicitacaoExameDAO().pesquisarItemSolicitacaoExamePorSolicitacaoExameComHrColeta(soeSeq);	
		}
		if(listaItens==null || listaItens.size()==0){
			verificaColeta = false;
		}
		
		List<DominioDiaSemana> listaDias = null;
		List<Integer> listaHorariosIniciais = null;
		List<Integer> listaHorariosFinais = null;
		if(verificaColeta){
    		List<AelExameHorarioColeta> listaExames = this.getAelExameHorarioColetaDAO().pesquisarExameHorarioColetaPorSolicitacao(soeSeq);
    		Double minutos = null;
    		Double menorSomatorio = null;
    		String siglaAux = null;
    		Integer matExameAux = null;
    		for(AelExameHorarioColeta exameHorario: listaExames){
    			if(minutos == null){
    				minutos = DateUtil.getMinutos(exameHorario.getHorarioFinal())-DateUtil.getMinutos(exameHorario.getHorarioInicial());
    				menorSomatorio = minutos;
    			} else {
    				minutos = DateUtil.getMinutos(exameHorario.getHorarioFinal())-DateUtil.getMinutos(exameHorario.getHorarioInicial());
    				if(minutos<menorSomatorio){
    					menorSomatorio = minutos;
    					siglaAux = exameHorario.getId().getEmaExaSigla();
    					matExameAux = exameHorario.getId().getEmaManSeq();
    				}
    			}
    		}
    		/*	Codigo comentado, pois foi migrado do agh porem nao faz sentido no escopo atual
    		 * if(sigla!= null && matExame!=null){
    			listaExamesHorarioColeta = this.getAelExameHorarioColetaDAO().pesquisarExameHorarioColeta(siglaAux, matExameAux);
    			for(AelExameHorarioColeta exameHorarioColeta: listaExamesHorarioColeta){
    				DominioTurno turno = this.getAmbulatorioFacade().obterTurno(exameHorarioColeta.getHorarioInicial());
    				if(turno.equals(DominioTurno.M)){
    					pesquisaPorDiaHora = true;
    				}		
    			}
    		}*/
    		listaExamesHorarioColeta = this.getAelExameHorarioColetaDAO().pesquisarExameHorarioColeta(siglaAux, matExameAux);
    		listaDias = new ArrayList<DominioDiaSemana>();
			listaHorariosIniciais = new ArrayList<Integer>();
			listaHorariosFinais = new ArrayList<Integer>();
        	if(listaExamesHorarioColeta!=null){
        		for(AelExameHorarioColeta exameHorarioColeta: listaExamesHorarioColeta){
    				DominioDiaSemana dia = CoreUtil.retornaDiaSemana(exameHorarioColeta.getHorarioInicial());
    				Integer horaInicial = DateUtil.getHoras(exameHorarioColeta.getHorarioInicial());
    				Integer horaFinal = DateUtil.getHoras(exameHorarioColeta.getHorarioFinal());
    				listaDias.add(dia);
    				listaHorariosIniciais.add(horaInicial);
    				listaHorariosFinais.add(horaFinal);
    			}
        	}
        }
		
		List<VAelHrGradeDispVO> listaVO = this.getVAelHrGradeDispDAO().pesquisarHorariosLivresParaExameVO(sigla, matExame, unfExame, dataReativacao, tipoMarcacao, tipoMarcacaoDiv, verificaColeta, listaExamesHorarioColeta, data, hora, grade, grupoExame, salaExecutoraExame, servidor);
		
		for(int i=0;i< listaVO.size();i++){
			VAelHrGradeDispVO vAelHrGradeDispVO = listaVO.get(i);
			vAelHrGradeDispVO.setId(i);
			if(vAelHrGradeDispVO.getMatricula()!=null && vAelHrGradeDispVO.getVinCodigo()!=null){
				RapServidoresId servidorId = new RapServidoresId();
				servidorId.setMatricula(vAelHrGradeDispVO.getMatricula());
				servidorId.setVinCodigo(vAelHrGradeDispVO.getVinCodigo());
				RapServidores servidorAux = this.getRegistroColaboradorFacade().obterRapServidoresPorChavePrimaria(servidorId);
				if(servidorAux.getPessoaFisica()!=null){
					vAelHrGradeDispVO.setResponsavel(vAelHrGradeDispVO.getVinCodigo()+_HIFEN_+ vAelHrGradeDispVO.getMatricula()+_HIFEN_+servidorAux.getPessoaFisica().getNome());
				}
			}
			if(vAelHrGradeDispVO.getGrupoExame()!=null){
				AelGrupoExames grupoExameAux = this.getAelGrupoExamesDAO().obterPorChavePrimaria(vAelHrGradeDispVO.getGrupoExame().intValue());
				vAelHrGradeDispVO.setDescricaoGrupo(grupoExameAux.getDescricao());
			}
		}
		return listaVO;
	}
	
	public List<GradeHorarioExtraVO> pesquisarGradeExame(Object parametro, Short grade, String sigla, Integer matExame, Short unfExame) {
		List<GradeHorarioExtraVO> listaGradeHorario = new ArrayList<GradeHorarioExtraVO>();
		List<VAelGradeExame> lista = this.getVAelGradeExameDAO().pesquisarGradeExame(parametro, grade, sigla, matExame, unfExame);
		for(VAelGradeExame gradeExame: lista){
			GradeHorarioExtraVO gradeHorarioExtraVO = new GradeHorarioExtraVO();
			List<String> listaNomeUsualExameMatAnalise = null;
			if(gradeExame.getId()!=null){
				listaNomeUsualExameMatAnalise = this.getVAelExameMatAnaliseDAO().pesquisarNomeUsualExameMatAnalisePorSiglaEMaterial(gradeExame.getId().getSiglaExame(),gradeExame.getId().getMatExame());
				if(listaNomeUsualExameMatAnalise!=null){
					String nomeUsual  = listaNomeUsualExameMatAnalise.get(0);
					gradeHorarioExtraVO.setNomeUsualMaterial(nomeUsual);	
				}
			}
			gradeHorarioExtraVO.setSeqGrade(gradeExame.getId().getSeqGrade());
			gradeHorarioExtraVO.setGrade(gradeExame.getId().getGrade());
			gradeHorarioExtraVO.setGrupoExameSeq(gradeExame.getGrupoExame().intValue());
			gradeHorarioExtraVO.setDescrGrupoEx(gradeExame.getDescrGrupoEx());
			gradeHorarioExtraVO.setSiglaExame(gradeExame.getId().getSiglaExame());
			gradeHorarioExtraVO.setMatExame(gradeExame.getId().getMatExame());
			gradeHorarioExtraVO.setNumSala(gradeExame.getNumSala());
			gradeHorarioExtraVO.setNomeFunc(gradeExame.getNomeFunc());
			gradeHorarioExtraVO.setUnfExame(gradeExame.getId().getUnfExame());
			if(gradeHorarioExtraVO.getSiglaExame()!=null && gradeHorarioExtraVO.getNomeUsualMaterial()!=null){
				gradeHorarioExtraVO.setExame(gradeHorarioExtraVO.getSiglaExame()+_HIFEN_+gradeHorarioExtraVO.getNomeUsualMaterial());
			}
			listaGradeHorario.add(gradeHorarioExtraVO);			
		}
		return listaGradeHorario;
	}
	
	public List<VAelExaAgendPacVO> obterExamesAgendadosDoPaciente( Integer pacCodigo, String emaExaSigla, Integer emaManSeq,Short unfSeq) {
		List<VAelExaAgendPacVO> examesAgendados = new ArrayList<VAelExaAgendPacVO>();
		List<VAelExaAgendPac> listaExamesAgendados = this.getVAelExaAgendPacDAO().obterExamesAgendamentosPaciente(pacCodigo, emaExaSigla, emaManSeq, unfSeq);

		if(listaExamesAgendados!=null) {
			Integer id = 0;
			for (VAelExaAgendPac exameAgendado : listaExamesAgendados) {
				VAelExaAgendPacId exameAgendadoId = exameAgendado.getId();
				if (!verificaExistenciaHorario(exameAgendadoId.getHedGaeSeqp(),
					exameAgendadoId.getHedGaeUnfSeq(), exameAgendadoId.getHedDthrAgenda(), examesAgendados)) {
							VAelExaAgendPacVO exame = new VAelExaAgendPacVO();
							exame.setId(id);
							exame.setExameAgendadoPaciente(exameAgendado);
							id++;
							examesAgendados.add(exame);
						}
			}
		}

		if (!examesAgendados.isEmpty()) {
			Collections.sort(examesAgendados, new VAelExaAgendPacVOComparator());
		}
		
		return examesAgendados;

	}

	static class VAelExaAgendPacVOComparator implements Comparator<VAelExaAgendPacVO> {
		@Override
		public int compare(VAelExaAgendPacVO p1, VAelExaAgendPacVO p2) {
			return p1.getExameAgendadoPaciente().getId().getHedDthrAgenda().compareTo(p2.getExameAgendadoPaciente().getId().getHedDthrAgenda());
		}
	}

	public Boolean verificaExistenciaHorario(Integer hedGaeSeqp,
		Short hedGaeUnfSeq, Date hedDthrAgenda,
		List<VAelExaAgendPacVO> examesAgendados) {
		Boolean existe = false;
		for (VAelExaAgendPacVO exameAgendadoPac : examesAgendados) {
				if (exameAgendadoPac.getExameAgendadoPaciente().getId().getHedGaeSeqp().equals(hedGaeSeqp)
						&& exameAgendadoPac.getExameAgendadoPaciente().getId().getHedGaeUnfSeq().equals(hedGaeUnfSeq)
						&& exameAgendadoPac.getExameAgendadoPaciente().getId().getHedDthrAgenda().equals(hedDthrAgenda)) {
							existe = true;
							break;
				}
		}	
		return existe;
	}

	public List<VAelExaAgendPac> obterInformacoesExamesAgendadosPaciente(Integer pacCodigo, String emaExaSigla, 
			Integer emaManSeq, Short unfSeq, Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda) {
		List<VAelExaAgendPac> listaInformacoesExames = new ArrayList<VAelExaAgendPac>();
		listaInformacoesExames = this.getVAelExaAgendPacDAO().obterInformacoesExamesAgendadosPaciente(pacCodigo,
					emaExaSigla, emaManSeq, unfSeq, hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda);
		
		return listaInformacoesExames;
	}
	
	public void gravarHorarioExtra(AelHorarioExameDisp horarioExameDisp) throws BaseException{
		if(horarioExameDisp.getId()!=null && horarioExameDisp.getId().getDthrAgenda()!=null){
			Date dataDisponivel = horarioExameDisp.getId().getDthrAgenda();
			if(dataDisponivel.before(new Date())){
				throw new ApplicationBusinessException(ConsultaHorarioLivreONExceptionCode.DATA_HORA_MAIOR_QUE_DATA_ATUAL);
			}
		}
		if (getAelHorarioExameDispDAO().obterPorChavePrimaria(horarioExameDisp.getId()) == null) {
			IExamesFacade examesFacade = getExamesFacade();
			examesFacade.inserirHorarioExameDisp(horarioExameDisp);
			// O flush abaixo é necessário pois após o insert de horarioExameDisp 
			// é chamado um update para esta instância na enforce da AelItemHorarioAgendado.
			this.flush();				
		}
	}
	
	public Date obterDataHoraDisponivelParaGradeEUnidadeExecutora(Short unfExecutora, Integer grade, Date dataHora){
		AelHorarioExameDispId id = new AelHorarioExameDispId();
        id.setGaeUnfSeq(unfExecutora);
        id.setGaeSeqp(grade);
        id.setDthrAgenda(dataHora);
        AelHorarioExameDisp horarioExameDisp = this.getAelHorarioExameDispDAO().obterPorChavePrimaria(id);
        
        if (horarioExameDisp == null) {
        	return dataHora;
        } 
        
        while (horarioExameDisp != null && !DominioSituacaoHorario.L.equals(horarioExameDisp.getSituacaoHorario())) {
        	dataHora = DateUtil.adicionaMinutos(dataHora, 1);
        	id.setDthrAgenda(dataHora);
        	horarioExameDisp = this.getAelHorarioExameDispDAO().obterPorChavePrimaria(id);
        }
        
        return dataHora;
	}
	
	public List<ItemHorarioAgendadoVO> pesquisarAgendamentoPacientePorDatas(Integer pacCodigo, Date data1, Date data2){
		List<ItemHorarioAgendadoVO> listaAgendamento = new ArrayList<ItemHorarioAgendadoVO>();
		List<AelItemHorarioAgendado> listaItens = this.getAelItemHorarioAgendadoDAO().pesquisarAgendamentoPacientePorDatas(pacCodigo, data1, data2);
		for(AelItemHorarioAgendado itemHorarioAgendado: listaItens){
			ItemHorarioAgendadoVO itemHorarioAgendadoVO =  new ItemHorarioAgendadoVO();		
			if(itemHorarioAgendado.getItemSolicitacaoExame()!=null){
				itemHorarioAgendadoVO.setSeqp(itemHorarioAgendado.getItemSolicitacaoExame().getId().getSeqp());
				if(itemHorarioAgendado.getItemSolicitacaoExame().getSolicitacaoExame()!=null){
					itemHorarioAgendadoVO.setSoeSeq(itemHorarioAgendado.getItemSolicitacaoExame().getSolicitacaoExame().getSeq());
				}
				if(itemHorarioAgendado.getItemSolicitacaoExame().getExame()!=null){
					itemHorarioAgendadoVO.setDescricaoExame(itemHorarioAgendado.getItemSolicitacaoExame().getExame().getDescricaoUsual());
					itemHorarioAgendadoVO.setSigla(itemHorarioAgendado.getItemSolicitacaoExame().getExame().getSigla());
				}
				if(itemHorarioAgendado.getItemSolicitacaoExame().getMaterialAnalise()!=null){
					itemHorarioAgendadoVO.setSeqMaterialAnalise(itemHorarioAgendado.getItemSolicitacaoExame().getMaterialAnalise().getSeq());
					itemHorarioAgendadoVO.setDescricaoMaterialAnalise(itemHorarioAgendado.getItemSolicitacaoExame().getMaterialAnalise().getDescricao());
				}
				if(itemHorarioAgendado.getItemSolicitacaoExame().getUnidadeFuncional()!=null){
					itemHorarioAgendadoVO.setSeqUnidade(itemHorarioAgendado.getItemSolicitacaoExame().getUnidadeFuncional().getSeq());
					itemHorarioAgendadoVO.setDescricaoUnidade(itemHorarioAgendado.getItemSolicitacaoExame().getUnidadeFuncional().getDescricao());
				}
				SimpleDateFormat dataFormatada1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				itemHorarioAgendadoVO.setDthrAgenda(dataFormatada1.format(itemHorarioAgendado.getId().getHedDthrAgenda()));
				SimpleDateFormat dataFormatada = new SimpleDateFormat("dd/MM/yyyy");
				SimpleDateFormat horaFormatada = new SimpleDateFormat("HH:mm");
				itemHorarioAgendadoVO.setDataAgendamento(dataFormatada.format(itemHorarioAgendado.getId().getHedDthrAgenda()));
				itemHorarioAgendadoVO.setHoraAgendamento(horaFormatada.format(itemHorarioAgendado.getId().getHedDthrAgenda()));
			}
			listaAgendamento.add(itemHorarioAgendadoVO);
		}
		return listaAgendamento;
	}
	
	public List<ItemHorarioAgendadoVO> obterListaExamesParaAgendamentoEmGrupo(Integer soeSeq, List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO, Short hedGaeUnfSeq,
			Integer hedGaeSeqp, Short seqp) throws ApplicationBusinessException {
		List<ItemHorarioAgendadoVO> listaExamesAgendamentoEmGrupo = new ArrayList<ItemHorarioAgendadoVO>();
		List<Short> listaSeqps = new ArrayList<Short>();
		AghParametros paramSituacaoColetar = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_A_COLETAR);
		AghParametros paramSituacaoExecutar = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_A_EXECUTAR);

		String v_situacao_coletar = paramSituacaoColetar.getVlrTexto().substring(0, 2);
		String v_situacao_executar = paramSituacaoExecutar.getVlrTexto().substring(0, 2);

		String[] codigos = new String[] { v_situacao_coletar, v_situacao_executar };
		
		if(listaItemHorarioAgendadoVO.size()>1) {
			for(ItemHorarioAgendadoVO item : listaItemHorarioAgendadoVO) {
				if(!item.getSeqp().equals(seqp)) {
					listaSeqps.add(item.getSeqp());
				}
			}
			listaExamesAgendamentoEmGrupo = this.getAelItemSolicitacaoExameDAO().obterListaExamesParaAgendamentoEmGrupo(soeSeq,
				listaSeqps, hedGaeUnfSeq, hedGaeSeqp, codigos);
		}
		
		return listaExamesAgendamentoEmGrupo;
		
	}
	
	public List<ItemHorarioAgendadoVO> atualizarListaItemHorarioAgendadoVO( List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO,
			Short unfGrade, Integer gradeSeqp, String sala, Integer soeSeq) {
		
		List<AelItemHorarioAgendado> listaHorarioAgendado =	getAelItemHorarioAgendadoDAO().pesquisarItemHorarioAgendadoPorGradeESoeSeq(unfGrade, gradeSeqp, soeSeq);
		
		for (AelItemHorarioAgendado itemHorarioAgendado : listaHorarioAgendado) {
			for (ItemHorarioAgendadoVO itemHorarioAgendadoVO : listaItemHorarioAgendadoVO) {
				AelItemHorarioAgendadoId itemHorarioAgendadoId = itemHorarioAgendado.getId();
				if (itemHorarioAgendado.getId() != null 
						&& itemHorarioAgendadoId.getHedGaeUnfSeq().equals(unfGrade)
						&& itemHorarioAgendadoId.getHedGaeSeqp().equals(gradeSeqp)
						&& itemHorarioAgendadoId.getIseSoeSeq().equals(itemHorarioAgendadoVO.getSoeSeq()) 
						&& itemHorarioAgendadoId.getIseSeqp().equals(itemHorarioAgendadoVO.getSeqp())) {
					SimpleDateFormat dataFormatada1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					if(itemHorarioAgendadoVO.getDthrAgenda()!= null && !itemHorarioAgendadoVO.getDthrAgenda().equals(dataFormatada1.format(itemHorarioAgendadoId.getHedDthrAgenda()))){
						String dthrAgendaAnterior = itemHorarioAgendadoVO.getDthrAgenda();
						dthrAgendaAnterior = dthrAgendaAnterior.concat(_HIFEN_).concat(dataFormatada1.format(itemHorarioAgendadoId.getHedDthrAgenda()));
						itemHorarioAgendadoVO.setDthrAgenda(dthrAgendaAnterior);
					}else{
						itemHorarioAgendadoVO.setDthrAgenda(dataFormatada1.format(itemHorarioAgendadoId.getHedDthrAgenda()));
					}
					itemHorarioAgendadoVO.setSala(sala);
					itemHorarioAgendadoVO.setGrade(itemHorarioAgendadoId.getHedGaeSeqp());
					itemHorarioAgendadoVO.setItemHorarioAgendadoId(itemHorarioAgendado.getId());
					itemHorarioAgendadoVO.setCodigoSituacao(itemHorarioAgendado.getItemSolicitacaoExame()
							.getSituacaoItemSolicitacao().getCodigo());
					itemHorarioAgendadoVO.setDescricaoSituacao(itemHorarioAgendado.getItemSolicitacaoExame()
							.getSituacaoItemSolicitacao().getDescricao());
				}
			}
		}
		
		return listaItemHorarioAgendadoVO;
	}
			
	private VAelHrGradeDispDAO getVAelHrGradeDispDAO() {
		return vAelHrGradeDispDAO;
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	
	private AelGrupoExamesDAO getAelGrupoExamesDAO() {
		return aelGrupoExamesDAO;
	}
	
	private AelExameHorarioColetaDAO getAelExameHorarioColetaDAO() {
		return aelExameHorarioColetaDAO;
	}
	
	protected AelHorarioExameDispDAO getAelHorarioExameDispDAO() {
		return aelHorarioExameDispDAO;
	}
	
	private VAelExaAgendPacDAO getVAelExaAgendPacDAO() {
		return vAelExaAgendPacDAO;	
	}

	protected VAelExameMatAnaliseDAO getVAelExameMatAnaliseDAO() {
		return vAelExameMatAnaliseDAO;
	}
	
	protected VAelGradeExameDAO getVAelGradeExameDAO() {
		return vAelGradeExameDAO;
	}
	
	protected AelItemHorarioAgendadoDAO getAelItemHorarioAgendadoDAO() {
		return aelItemHorarioAgendadoDAO;
	}
	
	private AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	protected ConsultaHorarioLivreRN getConsultaHorarioLivreRN() {
		return consultaHorarioLivreRN;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

}
