package br.gov.mec.aghu.blococirurgico.cedenciasala.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.cedenciasala.vo.CedenciaSalasEntreEquipesEquipeVO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaractSalaEspDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaracteristicaSalaCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcHorarioTurnoCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSubstEscalaSalaDAO;
import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesDAO;
import br.gov.mec.aghu.configuracao.dao.AghProfEspecialidadesDAO;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcSubstEscalaSala;
import br.gov.mec.aghu.model.MbcSubstEscalaSalaId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.util.AghuEnumUtils;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
@SuppressWarnings("PMD.NPathComplexity")
public class CedenciaSalasEntreEquipeON extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(CedenciaSalasEntreEquipeON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcSubstEscalaSalaDAO mbcSubstEscalaSalaDAO;

	@Inject
	private MbcCaractSalaEspDAO mbcCaractSalaEspDAO;
	
	@Inject
	private MbcCaracteristicaSalaCirgDAO mbcCaracteristicaSalaCirgDAO;
	
	@Inject
	private MbcHorarioTurnoCirgDAO mbcHorarioTurnoCirg;	

	@EJB
	private CedenciaSalasEntreEquipeRN cedenciaSalasEntreEquipeRN;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;
	
	@Inject
	private AghProfEspecialidadesDAO aghProfEspecialidadesDAO;
	
	@Inject
	private AghEspecialidadesDAO aghEspecialidadesDAO;
	
	protected enum CedenciaSalasEntreEquipeONExceptionCode implements BusinessExceptionCode {
		MBC_01111, MBC_01112, MBC_01113, MBC_01117, 
		CEDENCIA_DE_SALA_INSTITUCIONAL_PARA_EQUIPE_REGISTRO_DUPLICADO, MENSAGEM_CEDENCIA_NAO_PODE_SER_DESATIVADA,
		DATA_INFERIOR_ATUAL, HORA_ATUAL_SUPERIOR_PERIODO;
	}

	private static final long serialVersionUID = 1139968295951781728L;
	
	public void atualizarMbcSubstEscalaSala(MbcSubstEscalaSala mbcSubstEscalaSala) throws ApplicationBusinessException {
		mbcSubstEscalaSala.setIndSituacao(DominioSituacao.getInstance(!mbcSubstEscalaSala.getIndSituacao().isAtivo()));//Recebe a negação do status atual
		mbcSubstEscalaSala.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		getMbcSubstEscalaSalaDAO().atualizar(mbcSubstEscalaSala);
		getCedenciaSalasEntreEquipeRN().posUpdateMbcSubstEscalaSala(mbcSubstEscalaSala);
	}
	
	public Integer gravarMbcSubstEscalaSala(MbcCaractSalaEsp mbcCaractSalaEsp, CedenciaSalasEntreEquipesEquipeVO cedenciaSalasEntreEquipesEquipeVO) throws ApplicationBusinessException {
		validarDatas(cedenciaSalasEntreEquipesEquipeVO, mbcCaractSalaEsp);
		Integer recorrencias = 0;
		if(cedenciaSalasEntreEquipesEquipeVO.getRecorrencia()){
			
			Date data = cedenciaSalasEntreEquipesEquipeVO.getData();
			while (DateUtil.validaDataMaiorIgual(cedenciaSalasEntreEquipesEquipeVO.getDataFim(), data)) {
				MbcSubstEscalaSala mbcSubstEscalaSala = new MbcSubstEscalaSala();
				preencherMbcSubstEscalaSala(mbcCaractSalaEsp, mbcSubstEscalaSala, cedenciaSalasEntreEquipesEquipeVO, data);
//				mbcSubstEscalaSala.getId().setData(data);
				MbcSubstEscalaSala mbcSubstEscalaSalaOriginal = getMbcSubstEscalaSalaDAO().obterPorChavePrimaria(mbcSubstEscalaSala.getId());
				if(mbcSubstEscalaSalaOriginal != null){
					if(mbcSubstEscalaSalaOriginal.getIndSituacao().equals(DominioSituacao.I)){
						mbcSubstEscalaSalaOriginal.setIndSituacao(DominioSituacao.A);
						getMbcSubstEscalaSalaDAO().persistir(mbcSubstEscalaSalaOriginal);
					}
				}else{
					getMbcSubstEscalaSalaDAO().persistir(mbcSubstEscalaSala);
				}
				
				recorrencias++;
				data = DateUtil.adicionaDias(mbcSubstEscalaSala.getId().getData(), 7 * cedenciaSalasEntreEquipesEquipeVO.getIntervalo());
			}
		}else{
			MbcSubstEscalaSala mbcSubstEscalaSala = new MbcSubstEscalaSala();
			preencherMbcSubstEscalaSala(mbcCaractSalaEsp, mbcSubstEscalaSala, cedenciaSalasEntreEquipesEquipeVO, cedenciaSalasEntreEquipesEquipeVO.getData());
			
			if(getMbcSubstEscalaSalaDAO().obterPorChavePrimaria(mbcSubstEscalaSala.getId()) != null){
				throw new ApplicationBusinessException(CedenciaSalasEntreEquipeONExceptionCode.CEDENCIA_DE_SALA_INSTITUCIONAL_PARA_EQUIPE_REGISTRO_DUPLICADO);
			}
			getMbcSubstEscalaSalaDAO().persistir(mbcSubstEscalaSala);
		}
		return recorrencias;
	}
	
	private void preencherMbcSubstEscalaSala(
			MbcCaractSalaEsp mbcCaractSalaEsp,
			MbcSubstEscalaSala mbcSubstEscalaSala,
			CedenciaSalasEntreEquipesEquipeVO cedenciaSalasEntreEquipesEquipeVO,
			Date data) throws ApplicationBusinessException {
		MbcSubstEscalaSalaId id = new MbcSubstEscalaSalaId();
		id.setCseCasSeq(mbcCaractSalaEsp.getId().getCasSeq());
		id.setCseEspSeq(mbcCaractSalaEsp.getId().getEspSeq());
		id.setCseSeqp(mbcCaractSalaEsp.getId().getSeqp());
		id.setData(data);
		id.setPucIndFuncaoProf((DominioFuncaoProfissional) cedenciaSalasEntreEquipesEquipeVO.getEquipeSubstituta().getObject());
		id.setPucSerMatricula(cedenciaSalasEntreEquipesEquipeVO.getEquipeSubstituta().getNumero11().intValue());
		id.setPucSerVinCodigo(cedenciaSalasEntreEquipesEquipeVO.getEquipeSubstituta().getNumero4());
		id.setPucUnfSeq(cedenciaSalasEntreEquipesEquipeVO.getEquipeSubstituta().getNumero5());
		
		mbcSubstEscalaSala.setId(id);
		mbcSubstEscalaSala.setCriadoEm(new Date());
		mbcSubstEscalaSala.setIndSituacao(DominioSituacao.A);
		mbcSubstEscalaSala.setRapServidores(getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(cedenciaSalasEntreEquipesEquipeVO.getUsuarioLogado()));
		mbcSubstEscalaSala.setMbcCaractSalaEsp(mbcCaractSalaEsp);
		mbcSubstEscalaSala.setPreEspSeq(cedenciaSalasEntreEquipesEquipeVO.getEquipeSubstituta().getNumero12());
		
		persistirProfEspecialidadeSub(mbcSubstEscalaSala, cedenciaSalasEntreEquipesEquipeVO);
	}

	
	public void persistirProfEspecialidadeSub(MbcSubstEscalaSala mbcSubstEscalaSala,CedenciaSalasEntreEquipesEquipeVO cedenciaSalasEntreEquipesEquipeVO){
		AghProfEspecialidades aghProfEspecialidades = new AghProfEspecialidades();
		
		aghProfEspecialidades = aghProfEspecialidadesDAO.findById(
			   cedenciaSalasEntreEquipesEquipeVO.getEquipeSubstituta().getNumero11(),
			   cedenciaSalasEntreEquipesEquipeVO.getEquipeSubstituta().getNumero4(),
			   cedenciaSalasEntreEquipesEquipeVO.getEquipeSubstituta().getNumero12());
		aghProfEspecialidades.setAghEspecialidade(aghEspecialidadesDAO.buscarEspecialidadesPorSeq(cedenciaSalasEntreEquipesEquipeVO.getEquipeSubstituta().getNumero12()));
		
		aghProfEspecialidadesDAO.persistir(aghProfEspecialidades);
		mbcSubstEscalaSala.setAghProfEspecialidades(aghProfEspecialidades);
	}
		
	public void validarDatas(CedenciaSalasEntreEquipesEquipeVO cedenciaSalasEntreEquipesEquipeVO, MbcCaractSalaEsp mbcCaractSalaEsp) throws ApplicationBusinessException {
		MbcCaracteristicaSalaCirg salaCir = (mbcCaractSalaEsp.getMbcCaracteristicaSalaCirg().getSeq() != null) ? this.mbcCaracteristicaSalaCirgDAO.obterPorChavePrimaria(mbcCaractSalaEsp.getMbcCaracteristicaSalaCirg().getSeq()): null;
		
		
		if (DateUtil.validaDataTruncadaMaior(new Date(), cedenciaSalasEntreEquipesEquipeVO.getData())) {
			throw new ApplicationBusinessException(CedenciaSalasEntreEquipeONExceptionCode.DATA_INFERIOR_ATUAL);
		}		
		if (DateUtil.truncaData(new Date()).equals(DateUtil.truncaData(cedenciaSalasEntreEquipesEquipeVO.getData()))) {			
			MbcHorarioTurnoCirg horTurnoCir = (salaCir != null) ? this.mbcHorarioTurnoCirg.obterPorChavePrimaria(salaCir.getMbcHorarioTurnoCirg().getId()): null;			
			if (horTurnoCir != null && DateUtil.validaHoraMaior(new Date(), horTurnoCir.getHorarioInicial())) {
				throw new ApplicationBusinessException(CedenciaSalasEntreEquipeONExceptionCode.HORA_ATUAL_SUPERIOR_PERIODO);
			}
		}
		if(cedenciaSalasEntreEquipesEquipeVO.getRecorrencia()){
			if(DateUtil.validaDataMaior(cedenciaSalasEntreEquipesEquipeVO.getData(), cedenciaSalasEntreEquipesEquipeVO.getDataFim())){
				throw new ApplicationBusinessException(CedenciaSalasEntreEquipeONExceptionCode.MBC_01112);
			}
			if(cedenciaSalasEntreEquipesEquipeVO.getIntervalo() < 1 || cedenciaSalasEntreEquipesEquipeVO.getIntervalo() > 52 ){
				throw new ApplicationBusinessException(CedenciaSalasEntreEquipeONExceptionCode.MBC_01113);
			}
		}
		if(salaCir != null && ! AghuEnumUtils.retornaDiaSemanaAghu(cedenciaSalasEntreEquipesEquipeVO.getData()).equals(salaCir.getDiaSemana()) ){
			throw new ApplicationBusinessException(CedenciaSalasEntreEquipeONExceptionCode.MBC_01117);
		}
	}
	
	protected MbcSubstEscalaSalaDAO getMbcSubstEscalaSalaDAO(){
		return mbcSubstEscalaSalaDAO;
	}
	
	protected MbcCaractSalaEspDAO getMbcCaractSalaEspDAO(){
		return mbcCaractSalaEspDAO;
	}
	
	protected MbcAgendasDAO getMbcAgendasDAO(){
		return mbcAgendasDAO;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.iRegistroColaboradorFacade;
	}
	
	protected CedenciaSalasEntreEquipeRN getCedenciaSalasEntreEquipeRN() {
		return cedenciaSalasEntreEquipeRN;
	}

	public void verificarProgramacaoAgendaSala(MbcSubstEscalaSala mbcSubstEscalaSala) throws ApplicationBusinessException {
		Long count = getMbcSubstEscalaSalaDAO().recuperarProgramacaoAgendaSalaCount(mbcSubstEscalaSala);//				
		
		if(count.intValue() > 0){
			List<MbcAgendas> agendas = getMbcAgendasDAO().
						buscarAgendasPorUnfSeqSalaData(
								mbcSubstEscalaSala.getMbcCaractSalaEsp().getMbcCaracteristicaSalaCirg().getMbcHorarioTurnoCirg(),
								mbcSubstEscalaSala.getMbcCaractSalaEsp().getMbcCaracteristicaSalaCirg().getMbcSalaCirurgica(),
								mbcSubstEscalaSala.getMbcProfAtuaUnidCirgs().getId().getUnfSeq(),
								mbcSubstEscalaSala.getId().getData());
			if(!agendas.isEmpty()){
				throw new ApplicationBusinessException(CedenciaSalasEntreEquipeONExceptionCode.MENSAGEM_CEDENCIA_NAO_PODE_SER_DESATIVADA, 
						mbcSubstEscalaSala.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNomeUsual() != null ? mbcSubstEscalaSala.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNomeUsual() 
								: mbcSubstEscalaSala.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
			}
		}		
	}
	
	public List<MbcCaractSalaEsp> listarMbcCaractSalaEspPorDiaSemana(Object objPesquisa, Date data){
		String pesquisa = objPesquisa != null ? objPesquisa.toString() : null;
		Calendar calendarDiaSemana = Calendar.getInstance();
		calendarDiaSemana.setTime(data);
		DominioDiaSemana diaSemana = DominioDiaSemana.getDiaDaSemana(calendarDiaSemana.get(Calendar.DAY_OF_WEEK));
		
		return getMbcCaractSalaEspDAO().listarMbcCaractSalaEspPorDiaSemana(pesquisa, diaSemana);
	}

    public Long pesquisarMbcCaractSalaEspPorDiaSemanaCount(
            Object objPesquisa, Date data){
        String pesquisa = objPesquisa != null ? objPesquisa.toString() : null;
        Calendar calendarDiaSemana = Calendar.getInstance();
        calendarDiaSemana.setTime(data);
        DominioDiaSemana diaSemana = DominioDiaSemana.getDiaDaSemana(calendarDiaSemana.get(Calendar.DAY_OF_WEEK));

        return getMbcCaractSalaEspDAO().pesquisarMbcCaractSalaEspPorDiaSemanaCount(pesquisa, diaSemana);
    }
}
