package br.gov.mec.aghu.blococirurgico.cedenciasala.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.cedenciasala.business.CedenciaSalasEntreEquipeON.CedenciaSalasEntreEquipeONExceptionCode;
import br.gov.mec.aghu.blococirurgico.cedenciasala.vo.CedenciaSalaInstitucionalParaEquipeVO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaracteristicaSalaCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCedenciaSalaHcpaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcHorarioTurnoCirgDAO;
import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesDAO;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcCedenciaSalaHcpa;
import br.gov.mec.aghu.model.MbcCedenciaSalaHcpaId;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.util.AghuEnumUtils;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class CedenciaSalaInstitucionalParaEquipeON extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(CedenciaSalaInstitucionalParaEquipeON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCaracteristicaSalaCirgDAO mbcCaracteristicaSalaCirgDAO;

	@Inject
	private MbcCedenciaSalaHcpaDAO mbcCedenciaSalaHcpaDAO;
	
	@Inject
	private AghEspecialidadesDAO aghEspecialidadesDAO;
	
	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcHorarioTurnoCirgDAO mbcHorarioTurnoCirgDAO;	

	
	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

private static final long serialVersionUID = -8981127762510753287L;
	
	protected enum CedenciaDeSalaInstitucionalParaEquipeONExceptionCode implements BusinessExceptionCode {
		MBC_01111, MBC_01112, MBC_01113, MBC_01117,MENSAGEM_CEDENCIA_NAO_PODE_SER_DESATIVADA,
		CEDENCIA_DE_SALA_INSTITUCIONAL_PARA_EQUIPE_REGISTRO_DUPLICADO, DATA_INFERIOR_ATUAL, HORA_ATUAL_SUPERIOR_PERIODO;
	}
	
	public Integer gravarCedenciaDeSalaInstitucionalParaEquipe(CedenciaSalaInstitucionalParaEquipeVO cedenciaSala,MbcCaracteristicaSalaCirg caracteristicaSalaCirg, AghEspecialidades especialidade) throws ApplicationBusinessException {
		validarDatas(cedenciaSala,caracteristicaSalaCirg);
		Integer recorrencias = 0;
		if(cedenciaSala.getRecorrencia()){
			while (DateUtil.validaDataMaiorIgual(cedenciaSala.getDataFim(), cedenciaSala.getData())) {
				MbcCedenciaSalaHcpa cedenciaSalaHcpa = new MbcCedenciaSalaHcpa();
				preencherMbcCedenciaSalaHcpa(cedenciaSala, cedenciaSalaHcpa, aghEspecialidadesDAO.obterPorChavePrimaria(especialidade.getSeq()));
				
				MbcCedenciaSalaHcpa cedenciaSalaHcpaOriginal = getMbcCedenciaSalaHcpaDAO().obterPorChavePrimaria(cedenciaSalaHcpa.getId());
				if(cedenciaSalaHcpaOriginal != null){
					if(cedenciaSalaHcpaOriginal.getIndSituacao().equals(DominioSituacao.I)){
						cedenciaSalaHcpaOriginal.setIndSituacao(DominioSituacao.A);
						getMbcCedenciaSalaHcpaDAO().persistir(cedenciaSalaHcpaOriginal);
					}
				}else{
					getMbcCedenciaSalaHcpaDAO().persistir(cedenciaSalaHcpa);
				}
				
				cedenciaSala.setData(DateUtil.adicionaDias(cedenciaSala.getData(), 7 * cedenciaSala.getIntervalo()));
				recorrencias++;
			}
		}else{
			MbcCedenciaSalaHcpa cedenciaSalaHcpa = new MbcCedenciaSalaHcpa();
			preencherMbcCedenciaSalaHcpa(cedenciaSala, cedenciaSalaHcpa, aghEspecialidadesDAO.obterPorChavePrimaria(especialidade.getSeq()));
			if(getMbcCedenciaSalaHcpaDAO().obterPorChavePrimaria(cedenciaSalaHcpa.getId()) != null){
				throw new ApplicationBusinessException(CedenciaDeSalaInstitucionalParaEquipeONExceptionCode.CEDENCIA_DE_SALA_INSTITUCIONAL_PARA_EQUIPE_REGISTRO_DUPLICADO);
			}
			mbcCedenciaSalaHcpaDAO.persistir(cedenciaSalaHcpa);
		}
		return recorrencias;
	}

	private void preencherMbcCedenciaSalaHcpa(CedenciaSalaInstitucionalParaEquipeVO cedenciaSala, MbcCedenciaSalaHcpa cedenciaSalaHcpa, AghEspecialidades especialidade) throws ApplicationBusinessException {
		MbcCedenciaSalaHcpaId id = new MbcCedenciaSalaHcpaId();
		id.setCasSeq(cedenciaSala.getCasSeq());
		id.setData(cedenciaSala.getData());
		id.setPucIndFuncaoProf((DominioFuncaoProfissional) cedenciaSala.getEquipe().getObject());
		id.setPucSerMatricula(cedenciaSala.getEquipe().getNumero11().intValue());
		id.setPucSerVinCodigo(cedenciaSala.getEquipe().getNumero4());
		id.setPucUnfSeq(cedenciaSala.getEquipe().getNumero5());
		cedenciaSalaHcpa.setId(id);
		cedenciaSalaHcpa.setIndSituacao(DominioSituacao.A);
		cedenciaSalaHcpa.setServidor(getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(cedenciaSala.getUsuarioLogado()));
		cedenciaSalaHcpa.setCriadoEm(new Date());
		cedenciaSalaHcpa.setEspecialidade(especialidade);
	}

	public void validarDatas(CedenciaSalaInstitucionalParaEquipeVO cedenciaSala,MbcCaracteristicaSalaCirg caracteristicaSalaCirg) throws ApplicationBusinessException {
		if (DateUtil.validaDataTruncadaMaior(new Date(), cedenciaSala.getData())) {
			throw new ApplicationBusinessException(CedenciaSalasEntreEquipeONExceptionCode.DATA_INFERIOR_ATUAL);
		}
		if (DateUtil.truncaData(new Date()).equals(DateUtil.truncaData(cedenciaSala.getData()))) {
			MbcHorarioTurnoCirg horTurnoCir = (caracteristicaSalaCirg != null) ? this.mbcHorarioTurnoCirgDAO.obterPorChavePrimaria(caracteristicaSalaCirg.getMbcHorarioTurnoCirg().getId()): null;			
			
			if (DateUtil.validaHoraMaior(new Date(), horTurnoCir.getHorarioInicial())) {
				throw new ApplicationBusinessException(CedenciaSalasEntreEquipeONExceptionCode.HORA_ATUAL_SUPERIOR_PERIODO);
			}
		}
		if(cedenciaSala.getRecorrencia()){
			if(DateUtil.validaDataMaior(cedenciaSala.getData(), cedenciaSala.getDataFim())){
				throw new ApplicationBusinessException(CedenciaDeSalaInstitucionalParaEquipeONExceptionCode.MBC_01112);
			}
			if(cedenciaSala.getIntervalo() < 1 || cedenciaSala.getIntervalo() > 52 ){
				throw new ApplicationBusinessException(CedenciaDeSalaInstitucionalParaEquipeONExceptionCode.MBC_01113);
			}
		}
		if(!AghuEnumUtils.retornaDiaSemanaAghu(cedenciaSala.getData()).equals(cedenciaSala.getDiaSemana()) ){
			throw new ApplicationBusinessException(CedenciaDeSalaInstitucionalParaEquipeONExceptionCode.MBC_01117);
		}
	}

	public void atualizarMbcCedenciaSalaHcpa(
			MbcCedenciaSalaHcpa mbcCedenciaSala) throws ApplicationBusinessException {
		mbcCedenciaSala.setServidor(servidorLogadoFacade.obterServidorLogado());
		getMbcCedenciaSalaHcpaDAO().atualizar(mbcCedenciaSala);
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade(){
		return iRegistroColaboradorFacade;
	}
	
	protected MbcCedenciaSalaHcpaDAO getMbcCedenciaSalaHcpaDAO(){
		return mbcCedenciaSalaHcpaDAO;
	}
	
	protected MbcCaracteristicaSalaCirgDAO getMbcCaracteristicaSalaCirgDAO(){
		return mbcCaracteristicaSalaCirgDAO;
	}
	
	protected MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}


	public void ativarInativarMbcCedenciaSalaHcpa(
			MbcCedenciaSalaHcpa mbcCedenciaSala) throws ApplicationBusinessException {
		mbcCedenciaSala.setIndSituacao(
				DominioSituacao.getInstance(
						!mbcCedenciaSala.getIndSituacao().isAtivo()));//Recebe a negação do status atual
		atualizarMbcCedenciaSalaHcpa(mbcCedenciaSala);
	}

	public void verificarProgramacaoAgendaSalaInstitucional(MbcCedenciaSalaHcpa cedenciaSelecionada) throws ApplicationBusinessException {
	//	Long count = getMbcCedenciaSalaHcpaDAO().recuperarProgramacaoAgendaSalaInstitucionalCount(cedenciaSelecionada);//				

		SimpleDateFormat formatador = new SimpleDateFormat("HH:mm");
		Integer count = 0;

		
		List<MbcAgendas> listaMbcAgendas = this.getMbcAgendasDAO().pesquisarPlanejamentoCirurgiaAgendada(
				cedenciaSelecionada.getId().getData(), cedenciaSelecionada.getId().getData(), 
				cedenciaSelecionada.getMbcProfAtuaUnidCirgs().getId().getSerMatricula(),
				cedenciaSelecionada.getMbcProfAtuaUnidCirgs().getId().getSerVinCodigo(),
				cedenciaSelecionada.getMbcProfAtuaUnidCirgs().getId().getUnfSeq(),
				cedenciaSelecionada.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf(), null, 
				cedenciaSelecionada.getId().getPucUnfSeq());

			for (MbcAgendas agenda : listaMbcAgendas){
				//  verificar se a sala é a mesma 
				if (agenda.getSalaCirurgica()!=null && (agenda.getSalaCirurgica().getId().getSeqp() == 
						cedenciaSelecionada.getMbcCaracteristicaSalaCirg().getMbcSalaCirurgica().getId().getSeqp())){
				// verificar se o turno é o mesmo	
					try {
						if ((formatador.parse(formatador.format(agenda.getDthrPrevInicio())).
								equals(formatador.parse(formatador.format(cedenciaSelecionada.getMbcCaracteristicaSalaCirg().getMbcHorarioTurnoCirg().getHorarioInicial())))
							||	formatador.parse(formatador.format(agenda.getDthrPrevInicio())).after(
							formatador.parse(formatador.format(cedenciaSelecionada.getMbcCaracteristicaSalaCirg().getMbcHorarioTurnoCirg().getHorarioInicial()))))
							&& 
							(formatador.parse(formatador.format(agenda.getDthrPrevFim())).equals(
									 formatador.parse(formatador.format(cedenciaSelecionada.getMbcCaracteristicaSalaCirg().getMbcHorarioTurnoCirg().getHorarioFinal())))
							||		formatador.parse(formatador.format(agenda.getDthrPrevFim())).before(
							 formatador.parse(formatador.format(cedenciaSelecionada.getMbcCaracteristicaSalaCirg().getMbcHorarioTurnoCirg().getHorarioFinal()))))){
							count = 1;
						}

					} catch (ParseException e) {
						LOG.error(e.getMessage());
					}
				}
			}
				
		
		if(count.intValue() > 0){
			throw new ApplicationBusinessException(CedenciaSalasEntreEquipeONExceptionCode.MENSAGEM_CEDENCIA_NAO_PODE_SER_DESATIVADA, 
					cedenciaSelecionada.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNomeUsual() != null ? cedenciaSelecionada.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNomeUsual() 
							: cedenciaSelecionada.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
		}

	}
	
	public List<MbcCaracteristicaSalaCirg> listarCaracteristicaSalaCirgPorDiaSemana(Object objPesquisa, Date data){
		String pesquisa = objPesquisa != null ? objPesquisa.toString() : null;
		Calendar calendarDiaSemana = Calendar.getInstance();
		calendarDiaSemana.setTime(data);
		DominioDiaSemana diaSemana = DominioDiaSemana.getDiaDaSemana(calendarDiaSemana.get(Calendar.DAY_OF_WEEK));
		
		return getMbcCaracteristicaSalaCirgDAO().listarCaracteristicaSalaCirgPorDiaSemana(pesquisa, diaSemana);
	}

    public Long pesquisaCaracteristicaSalaCirgPorDiaSemanaCount(Object objPesquisa, Date data) {
        String pesquisa = objPesquisa != null ? objPesquisa.toString() : null;
        Calendar calendarDiaSemana = Calendar.getInstance();
        calendarDiaSemana.setTime(data);
        DominioDiaSemana diaSemana = DominioDiaSemana.getDiaDaSemana(calendarDiaSemana.get(Calendar.DAY_OF_WEEK));

        return getMbcCaracteristicaSalaCirgDAO().pesquisaCaracteristicaSalaCirgPorDiaSemanaCount(pesquisa, diaSemana);
    }
}
