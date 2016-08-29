package br.gov.mec.aghu.exames.coleta.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.exames.agendamento.vo.ItemHorarioAgendadoVO;
import br.gov.mec.aghu.exames.agendamento.vo.VAelHrGradeDispVO;
import br.gov.mec.aghu.exames.coleta.vo.GrupoExameVO;
import br.gov.mec.aghu.exames.coleta.vo.OrigemUnidadeVO;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelGrupoExameUnidExameDAO;
import br.gov.mec.aghu.exames.dao.AelHorarioExameDispDAO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelHorarioExameDispId;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class TransferenciaAgendamentoON extends BaseBusiness {

private static final String _HIFEN_ = " - ";

private static final Log LOG = LogFactory.getLog(TransferenciaAgendamentoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;

@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@EJB
private IAgendamentoExamesFacade agendamentoExamesFacade;

@Inject
private AelGrupoExameUnidExameDAO aelGrupoExameUnidExameDAO;

@Inject
private AelItemHorarioAgendadoDAO aelItemHorarioAgendadoDAO;

@Inject
private AelUnfExecutaExamesDAO aelUnfExecutaExamesDAO;

@Inject
private AelHorarioExameDispDAO aelHorarioExameDispDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8072741678131764790L;
	
	public enum TransferenciaAgendamentoExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERROR_TRANSFERENCIA_AGENDAMENTO_NAO_MARCADO;
	}
	
	
	public List<ItemHorarioAgendadoVO> pesquisarExamesParaTransferencia(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda) {
		List<ItemHorarioAgendadoVO> listaRetorno = new ArrayList<ItemHorarioAgendadoVO>();
		List<AelItemHorarioAgendado> listaItens = this.getAelItemHorarioAgendadoDAO().pesquisarExamesParaTransferencia(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda);
		for(AelItemHorarioAgendado itemHorarioAgendado: listaItens){
			ItemHorarioAgendadoVO itemHorarioAgendadoVO = new ItemHorarioAgendadoVO();
			itemHorarioAgendadoVO.setSeqUnidade(itemHorarioAgendado.getItemSolicitacaoExame().getAelUnfExecutaExames().getUnidadeFuncional().getSeq());
			itemHorarioAgendadoVO.setGrade(itemHorarioAgendado.getId().getHedGaeSeqp());
			SimpleDateFormat dataFormatada1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			itemHorarioAgendadoVO.setDthrAgenda(dataFormatada1.format(itemHorarioAgendado.getId().getHedDthrAgenda()));
			itemHorarioAgendadoVO.setSoeSeq(itemHorarioAgendado.getItemSolicitacaoExame().getId().getSoeSeq());
			itemHorarioAgendadoVO.setSeqp(itemHorarioAgendado.getItemSolicitacaoExame().getId().getSeqp());
			itemHorarioAgendadoVO.setCodigoSituacao(itemHorarioAgendado.getItemSolicitacaoExame().getSituacaoItemSolicitacao().getCodigo());
			itemHorarioAgendadoVO.setDescricaoSituacao(itemHorarioAgendado.getItemSolicitacaoExame().getSituacaoItemSolicitacao().getDescricao());
			itemHorarioAgendadoVO.setDescricaoMaterialAnalise(itemHorarioAgendado.getItemSolicitacaoExame().getAelExameMaterialAnalise().getNomeUsualMaterial());
			itemHorarioAgendadoVO.setSigla(itemHorarioAgendado.getItemSolicitacaoExame().getAelExameMaterialAnalise().getId().getExaSigla());
			itemHorarioAgendadoVO.setSeqMaterialAnalise(itemHorarioAgendado.getItemSolicitacaoExame().getAelExameMaterialAnalise().getId().getManSeq());
			if(itemHorarioAgendado.getEtapaExame()!=null && itemHorarioAgendado.getEtapaExame().getNumero()!=null){
				itemHorarioAgendadoVO.setNroEtapa(itemHorarioAgendado.getEtapaExame().getNumero());	
			}
			if(itemHorarioAgendado.getItemSolicitacaoExame()!=null){
				AelItemSolicitacaoExamesId id = itemHorarioAgendado.getItemSolicitacaoExame().getId(); 
				List<AelAmostraItemExames> listaAmostras = this.getAelAmostraItemExamesDAO().pesquisarPorItemSolicitacaoExame(id);
				if(listaAmostras!=null && listaAmostras.size()>0){
					AelAmostraItemExames amostra = listaAmostras.get(0);
					if(amostra!=null){
						itemHorarioAgendadoVO.setNroAmostra(amostra.getId().getAmoSeqp());
					}
				}
			}
			listaRetorno.add(itemHorarioAgendadoVO);
		}
		return listaRetorno;
	}	
	
	public OrigemUnidadeVO obterOrigemUnidadeSolicitante(Short gaeUnfSeq, Integer gaeSeqp, Date dthrAgenda){
		OrigemUnidadeVO origemUnidadeVO = new OrigemUnidadeVO();
		List<AelItemHorarioAgendado> lista = this.getAelItemHorarioAgendadoDAO().pesquisarItemHorarioAgendadoPorGaeUnfSeqGaeSeqpDthrAgenda(gaeUnfSeq, gaeSeqp, dthrAgenda);
		if(lista!=null && lista.size()>0){
			AelItemHorarioAgendado itemHorarioAgendado = lista.get(0);
			if(itemHorarioAgendado!=null && itemHorarioAgendado.getItemSolicitacaoExame()!=null && itemHorarioAgendado.getItemSolicitacaoExame().getSolicitacaoExame()!=null){
				if(itemHorarioAgendado.getItemSolicitacaoExame().getSolicitacaoExame().getUnidadeFuncional()!=null){
					Short unfSeq = itemHorarioAgendado.getItemSolicitacaoExame().getSolicitacaoExame().getUnidadeFuncional().getSeq();
					origemUnidadeVO.setUnfSeq(unfSeq);
				}
				if(itemHorarioAgendado.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimento()!=null){
					DominioOrigemAtendimento origem = itemHorarioAgendado.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimento().getOrigem();
					origemUnidadeVO.setOrigem(origem);
				}
			}
			return origemUnidadeVO;
		}
		return null;
	}
	
	public List<ItemHorarioAgendadoVO> obterExamesSelecionados(List<ItemHorarioAgendadoVO> listaExamesAgendados){
		List<ItemHorarioAgendadoVO> lista = new ArrayList<ItemHorarioAgendadoVO>();
		for(ItemHorarioAgendadoVO itemHorarioAgendadoVO: listaExamesAgendados){
			if(itemHorarioAgendadoVO.getSelecionado()){
				lista.add(itemHorarioAgendadoVO);
			}
		}
		return lista;
	}
	
	public List<VAelHrGradeDispVO> pesquisarHorariosParaExameSelecionado(Date dataHoraReativacao, Short tipo1, Short tipo2,ItemHorarioAgendadoVO itemHorarioAgendadoVO, Date data, Date hora, Integer grade, AelGrupoExames grupoExame, AelSalasExecutorasExames salaExecutoraExame, RapServidores servidor){
		String sigla = itemHorarioAgendadoVO.getSigla();
		Integer manSeq = itemHorarioAgendadoVO.getSeqMaterialAnalise();
		Short unfSeq = itemHorarioAgendadoVO.getSeqUnidade();
		List<VAelHrGradeDispVO> listaRetorno = new ArrayList<VAelHrGradeDispVO>();
		List<AelHorarioExameDisp> listaHorarios = this.getAelHorarioExameDispDAO().pesquisarHorarioExameTransferenciaAgendamento(dataHoraReativacao, tipo1, tipo2, sigla, manSeq, unfSeq, data, hora, grade, grupoExame, salaExecutoraExame, servidor);
		for(int i=0; i<listaHorarios.size();i++){
			AelHorarioExameDisp horarioExameDisp = listaHorarios.get(i);
			VAelHrGradeDispVO vAelHrGradeDispVO = new VAelHrGradeDispVO();
			vAelHrGradeDispVO.setId(i);
			Date dthrAgenda = horarioExameDisp.getId().getDthrAgenda();
			SimpleDateFormat dataFormatada = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat horaFormatada = new SimpleDateFormat("HH:mm");
			vAelHrGradeDispVO.setDtAgenda(dataFormatada.format(dthrAgenda));
			vAelHrGradeDispVO.setHrAgenda(horaFormatada.format(dthrAgenda));
			vAelHrGradeDispVO.setDthrAgenda(dthrAgenda);
			vAelHrGradeDispVO.setHrExtra(horarioExameDisp.getIndHorarioExtra());
			
			
			if(horarioExameDisp.getGradeAgendaExame()!=null && horarioExameDisp.getGradeAgendaExame().getId()!=null){
				vAelHrGradeDispVO.setSeqGrade(horarioExameDisp.getGradeAgendaExame().getId().getSeqp());
				vAelHrGradeDispVO.setGrade(horarioExameDisp.getGradeAgendaExame().getId().getUnfSeq());	
			}
			vAelHrGradeDispVO.setUnfExame(unfSeq);
			if(horarioExameDisp.getGradeAgendaExame()!=null && horarioExameDisp.getGradeAgendaExame().getGrupoExame()!=null){
				vAelHrGradeDispVO.setDescricaoGrupo(horarioExameDisp.getGradeAgendaExame().getGrupoExame().getDescricao());
			}
			if(horarioExameDisp.getGradeAgendaExame()!=null && horarioExameDisp.getGradeAgendaExame().getSalaExecutoraExames()!=null && horarioExameDisp.getGradeAgendaExame().getSalaExecutoraExames().getNumero()!=null){
				vAelHrGradeDispVO.setDescricaoSala(horarioExameDisp.getGradeAgendaExame().getSalaExecutoraExames().getNumero());
			}
			if(horarioExameDisp.getGradeAgendaExame() != null && horarioExameDisp.getGradeAgendaExame().getServidor() != null){
				vAelHrGradeDispVO.setResponsavel(horarioExameDisp.getGradeAgendaExame().getServidor().getId().getVinCodigo()+_HIFEN_+ horarioExameDisp.getGradeAgendaExame().getServidor().getId().getMatricula()+_HIFEN_+horarioExameDisp.getGradeAgendaExame().getServidor().getPessoaFisica().getNome());
			}
			listaRetorno.add(vAelHrGradeDispVO);
		}
		return listaRetorno;
	}
	
	public List<VAelHrGradeDispVO> pesquisarHorariosParaGrupoExameSelecionado(Date dataHoraReativacao, Short tipo1, Short tipo2, List<GrupoExameVO> listaGrupoExame, Date data, Date hora, Integer grade, AelGrupoExames grupoExame, AelSalasExecutorasExames salaExecutoraExame, RapServidores servidor){
		List<VAelHrGradeDispVO> listaRetorno = new ArrayList<VAelHrGradeDispVO>();
		List<AelHorarioExameDisp> listaHorarios = this.getAelHorarioExameDispDAO().pesquisarHorarioGrupoExameTransferenciaAgendamento(dataHoraReativacao, tipo1, tipo2, listaGrupoExame, data, hora, grade, grupoExame, salaExecutoraExame, servidor);
		for(int i=0; i<listaHorarios.size();i++){
			AelHorarioExameDisp horarioExameDisp = listaHorarios.get(i);
			VAelHrGradeDispVO vAelHrGradeDispVO = new VAelHrGradeDispVO();
			vAelHrGradeDispVO.setId(i);
			Date dthrAgenda = horarioExameDisp.getId().getDthrAgenda();
			SimpleDateFormat dataFormatada = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat horaFormatada = new SimpleDateFormat("HH:mm");
			vAelHrGradeDispVO.setDtAgenda(dataFormatada.format(dthrAgenda));
			vAelHrGradeDispVO.setHrAgenda(horaFormatada.format(dthrAgenda));
			vAelHrGradeDispVO.setDthrAgenda(dthrAgenda);
			vAelHrGradeDispVO.setHrExtra(horarioExameDisp.getIndHorarioExtra());
			
			if(horarioExameDisp.getGradeAgendaExame()!=null && horarioExameDisp.getGradeAgendaExame().getId()!=null){
				vAelHrGradeDispVO.setSeqGrade(horarioExameDisp.getGradeAgendaExame().getId().getSeqp());
				vAelHrGradeDispVO.setGrade(horarioExameDisp.getGradeAgendaExame().getId().getUnfSeq());

			}
			vAelHrGradeDispVO.setUnfExame(null);
			if(horarioExameDisp.getGradeAgendaExame()!=null && horarioExameDisp.getGradeAgendaExame().getGrupoExame()!=null){
				vAelHrGradeDispVO.setDescricaoGrupo(horarioExameDisp.getGradeAgendaExame().getGrupoExame().getDescricao());
			}
			if(horarioExameDisp.getGradeAgendaExame()!=null && horarioExameDisp.getGradeAgendaExame().getSalaExecutoraExames()!=null && horarioExameDisp.getGradeAgendaExame().getSalaExecutoraExames().getNumero()!=null){
				vAelHrGradeDispVO.setDescricaoSala(horarioExameDisp.getGradeAgendaExame().getSalaExecutoraExames().getNumero());
			}
			vAelHrGradeDispVO.setResponsavel(horarioExameDisp.getServidor().getId().getVinCodigo()+_HIFEN_+ horarioExameDisp.getServidor().getId().getMatricula()+_HIFEN_+horarioExameDisp.getServidor().getPessoaFisica().getNome());
			listaRetorno.add(vAelHrGradeDispVO);
		}
		return listaRetorno;
	}
	
	public Date obterDataReativacaoUnfExecutaExameAtiva(final String emaExaSigla, final Integer emaManSeq, final Short unfSeq) {
		AelUnfExecutaExames unfExecutaExames = this.getAelUnfExecutaExamesDAO().obterDataReativacaoUnfExecutaExameAtiva(emaExaSigla, emaManSeq, unfSeq);
		if(unfExecutaExames!=null){
			return unfExecutaExames.getDthrReativaTemp();	
		} else {
			return null;
		}
	}
	
	public List<GrupoExameVO> pesquisarGrupoExameTransferenciaAgendamento(List<ItemHorarioAgendadoVO> listaItens) {
		return this.getAelGrupoExameUnidExameDAO().pesquisarGrupoExameTransferenciaAgendamento(listaItens);
	}

	public Date obterMaiorDataReativacao(List<GrupoExameVO> listaGrupoExame){
		Date dataHoraReativacao = null;
		List<Date> listaDatas = new ArrayList<Date>();
		for(GrupoExameVO grupoExame:listaGrupoExame){
			if(grupoExame.getDthrReativacao()!=null){
				listaDatas.add(grupoExame.getDthrReativacao()); 
			}
		}
		if(listaDatas!=null && listaDatas.size()>0){
			dataHoraReativacao = listaDatas.get(0);	
		}
		
		for(Date data: listaDatas){
			if(DateUtil.validaDataMaior(data,dataHoraReativacao)){
				dataHoraReativacao = data;
			}
		}
		return dataHoraReativacao;
	}
	
	
	public void transferirHorarioAgendado(AelItemHorarioAgendado itemHorarioAgendado, Boolean permiteHoraExtra, AelUnfExecutaExames unfExecutoraExame, String nomeMicrocomputador) throws BaseException {
		AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId();
		id.setSeqp(itemHorarioAgendado.getId().getIseSeqp());
		id.setSoeSeq(itemHorarioAgendado.getId().getIseSoeSeq());
		AelItemSolicitacaoExames itemSolicitacaoExame = this.getAelItemSolicitacaoExameDAO().obterPorChavePrimaria(id);
		AelHorarioExameDispId horarioExameDispId = new AelHorarioExameDispId();
		horarioExameDispId.setDthrAgenda(itemHorarioAgendado.getId().getHedDthrAgenda());
		horarioExameDispId.setGaeSeqp(itemHorarioAgendado.getId().getHedGaeSeqp());
		horarioExameDispId.setGaeUnfSeq(itemHorarioAgendado.getId().getHedGaeUnfSeq());
		AelHorarioExameDisp horarioExameDisp = this.getAelHorarioExameDispDAO().obterPorChavePrimaria(horarioExameDispId);
		itemHorarioAgendado.setHorarioExameDisp(horarioExameDisp);
		List<AelItemHorarioAgendado> listItensHorarioAgendado = getAelItemHorarioAgendadoDAO().buscarPorItemSolicitacaoExame(itemSolicitacaoExame);
		for(AelItemHorarioAgendado item : listItensHorarioAgendado) {
			getAelItemHorarioAgendadoDAO().remover(item);
		}
		getAgendamentoExamesFacade().inserirItemHorarioAgendado(itemHorarioAgendado, nomeMicrocomputador);
		getAgendamentoExamesFacade().verificarHorarioEscolhido(itemHorarioAgendado, itemSolicitacaoExame.getId().getSeqp(), 
				permiteHoraExtra, Boolean.FALSE, Boolean.TRUE, unfExecutoraExame, nomeMicrocomputador);
	}
	
	public void validarTransferenciaAgendamento(DominioSituacaoHorario situacao) throws BaseException {
		if(!DominioSituacaoHorario.M.equals(situacao)) {
			throw new ApplicationBusinessException(TransferenciaAgendamentoExceptionCode.MENSAGEM_ERROR_TRANSFERENCIA_AGENDAMENTO_NAO_MARCADO);
		}
	}
	
	protected AelGrupoExameUnidExameDAO getAelGrupoExameUnidExameDAO() {
		return aelGrupoExameUnidExameDAO;
	}
	
	protected AelItemHorarioAgendadoDAO getAelItemHorarioAgendadoDAO() {
		return aelItemHorarioAgendadoDAO;
	}
	
	protected AelAmostraItemExamesDAO getAelAmostraItemExamesDAO() {
		return aelAmostraItemExamesDAO;
	}
	
	protected AelHorarioExameDispDAO getAelHorarioExameDispDAO() {
		return aelHorarioExameDispDAO;
	}
	
	protected AelUnfExecutaExamesDAO getAelUnfExecutaExamesDAO() {
		return aelUnfExecutaExamesDAO;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	protected IAgendamentoExamesFacade getAgendamentoExamesFacade() {
		return this.agendamentoExamesFacade;
	}

}
