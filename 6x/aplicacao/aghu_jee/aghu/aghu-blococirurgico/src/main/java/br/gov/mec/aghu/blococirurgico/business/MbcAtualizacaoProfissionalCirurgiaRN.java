package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.MbcControleEscalaCirurgicaRN.MbcControleEscalaCirurgicaRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcEscalaProfUnidCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcEscalaProfUnidCirg;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;


@Stateless
public class MbcAtualizacaoProfissionalCirurgiaRN extends BaseBusiness {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1398620939000590270L;
	private static final Log LOG = LogFactory.getLog(MbcAtualizacaoProfissionalCirurgiaRN.class);
	

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private MbcProfCirurgiasRN mbcProfCirurgiasRN;
	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;
	
	@EJB
	private IPacienteFacade iPacienteFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IParametroFacade iParametroFacade;
	
	@EJB
	private MbcControleEscalaCirurgicaRN mbcControleEscalaCirurgicaRN;
	
	@Inject
	private MbcEscalaProfUnidCirgDAO mbcEscalaProfUnidCirgDAO;
	

	public void atualizarProfCirurgiaPorFuncao(RapServidores servidor, MbcCirurgias curCrg,	List<MbcEscalaProfUnidCirg> listaEscalaProfUnidCirg)
			throws BaseException {
		for(MbcEscalaProfUnidCirg escalaProfUnidCirg : listaEscalaProfUnidCirg ){
			/* Busca profissionais da cirurgia por função */
			List<MbcProfCirurgias> profCirurgias = this.getMbcProfCirurgiasDAO().listarMbcProfCirurgiasPorFuncao(curCrg.getSeq(), escalaProfUnidCirg.getId().getPucIndFuncaoProf());

			if(profCirurgias == null || profCirurgias.isEmpty()){
				MbcProfCirurgias profCirurgia = mbcControleEscalaCirurgicaRN.populaMbcProfCirurgias(curCrg, escalaProfUnidCirg);

				/* INSERT INTO	mbc_prof_cirurgias */
				this.getMbcProfCirurgiasRN().inserirMbcProfCirurgias(profCirurgia);

			}else if(profCirurgias.get(0).getIndInclEscala()){
				/* Delete mbc_prof_cirurgias */
				this.getMbcProfCirurgiasRN().removerMbcProfCirurgias(profCirurgias.get(0), Boolean.FALSE);

				MbcProfCirurgias profCirurgia = mbcControleEscalaCirurgicaRN.populaMbcProfCirurgias(curCrg, escalaProfUnidCirg);

				/* INSERT INTO	mbc_prof_cirurgias */
				this.getMbcProfCirurgiasRN().inserirMbcProfCirurgias(profCirurgia);
			}

		}//END LOOP
	}
	
	public void atualizarProfAnest(RapServidores servidor,
			String turno, MbcCirurgias curCrg, DominioDiaSemana diaSemana)
					throws BaseException {

		/* Busca anest professor */
		List<MbcProfCirurgias> anstProfCirurg = this.getMbcProfCirurgiasDAO().listarMbcProfCirurgiasPorFuncao(curCrg.getSeq(), DominioFuncaoProfissional.ANP);

		if(anstProfCirurg != null && !anstProfCirurg.isEmpty()){

			if(anstProfCirurg.get(0).getIndInclEscala()){

				/* Deleta Anestesista professor incluido pela escala */
				this.getMbcProfCirurgiasRN().removerMbcProfCirurgias(anstProfCirurg.get(0), Boolean.FALSE);

				/* Busca escala de profissionais de anestesia - professor */
				List<MbcEscalaProfUnidCirg> curEpuAnp  = this.getMbcEscalaProfUnidCirgDAO().pesquisarEscalaProfissionalPorFuncao(curCrg.getSalaCirurgica().getId().getUnfSeq(), curCrg.getSalaCirurgica().getId().getSeqp(), diaSemana, turno, DominioFuncaoProfissional.ANP);

				if(curEpuAnp != null && !curEpuAnp.isEmpty()){

					MbcProfCirurgias profCirurgia = mbcControleEscalaCirurgicaRN.populaMbcProfCirurgias(curCrg, curEpuAnp.get(0));

					/* INSERT INTO	mbc_prof_cirurgias */
					this.getMbcProfCirurgiasRN().inserirMbcProfCirurgias(profCirurgia);

				}else{
					/* Busca escala de profissionais de anestesia - contratado */
					List<MbcEscalaProfUnidCirg> curEpuAnc  = this.getMbcEscalaProfUnidCirgDAO().pesquisarEscalaProfissionalPorFuncao(curCrg.getSalaCirurgica().getId().getUnfSeq(), curCrg.getSalaCirurgica().getId().getSeqp(), diaSemana, turno, DominioFuncaoProfissional.ANC);

					if(curEpuAnc != null && !curEpuAnc.isEmpty()){

						MbcProfCirurgias profCirurgia = mbcControleEscalaCirurgicaRN.populaMbcProfCirurgias(curCrg, curEpuAnc.get(0));
						/* INSERT INTO	mbc_prof_cirurgias */
						this.getMbcProfCirurgiasRN().inserirMbcProfCirurgias(profCirurgia);
					}
				}
			}

		}else{

			/* Busca anest contratado */
			List<MbcProfCirurgias> anstContr = this.getMbcProfCirurgiasDAO().listarMbcProfCirurgiasPorFuncao(curCrg.getSeq(), DominioFuncaoProfissional.ANC);
			if(anstContr !=null && !anstContr.isEmpty()){

				if(anstContr.get(0).getIndInclEscala()){
					/* Deleta Anestesista contratado incluido pela escala */
					this.getMbcProfCirurgiasRN().removerMbcProfCirurgias(anstContr.get(0), Boolean.FALSE);

					/* Busca escala de profissionais de anestesia - prof */
					List<MbcEscalaProfUnidCirg> curEpuAnstProf  = this.getMbcEscalaProfUnidCirgDAO().pesquisarEscalaProfissionalPorFuncao(curCrg.getSalaCirurgica().getId().getUnfSeq(), curCrg.getSalaCirurgica().getId().getSeqp(), diaSemana, turno, DominioFuncaoProfissional.ANP);

					if(curEpuAnstProf != null && !curEpuAnstProf.isEmpty()){

						MbcProfCirurgias profCirurgia = mbcControleEscalaCirurgicaRN.populaMbcProfCirurgias(curCrg, curEpuAnstProf.get(0));
						/* INSERT INTO	mbc_prof_cirurgias */
						this.getMbcProfCirurgiasRN().inserirMbcProfCirurgias(profCirurgia);

					}else{

						/* Busca escala de profissionais de anestesia - contratado */
						List<MbcEscalaProfUnidCirg> curEpuAnc  = this.getMbcEscalaProfUnidCirgDAO().pesquisarEscalaProfissionalPorFuncao(curCrg.getSalaCirurgica().getId().getUnfSeq(), curCrg.getSalaCirurgica().getId().getSeqp(), diaSemana, turno, DominioFuncaoProfissional.ANC);

						if(curEpuAnc != null && !curEpuAnc.isEmpty()){

							MbcProfCirurgias profCirurgia = mbcControleEscalaCirurgicaRN.populaMbcProfCirurgias(curCrg, curEpuAnc.get(0));
							/* INSERT INTO	mbc_prof_cirurgias */
							this.getMbcProfCirurgiasRN().inserirMbcProfCirurgias(profCirurgia);
						}
					}
				}

			}else{

				/* Busca escala de profissionais de anestesia - professor */
				List<MbcEscalaProfUnidCirg> curEpuAnst  = this.getMbcEscalaProfUnidCirgDAO().pesquisarEscalaProfissionalPorFuncao(curCrg.getSalaCirurgica().getId().getUnfSeq(), curCrg.getSalaCirurgica().getId().getSeqp(), diaSemana, turno, DominioFuncaoProfissional.ANP);

				if(curEpuAnst != null && !curEpuAnst.isEmpty()){

					MbcProfCirurgias profCirurgia = mbcControleEscalaCirurgicaRN.populaMbcProfCirurgias(curCrg, curEpuAnst.get(0));
					/* INSERT INTO	mbc_prof_cirurgias */
					this.getMbcProfCirurgiasRN().inserirMbcProfCirurgias(profCirurgia);
				}else{

					/* Busca escala de profissionais de anestesia - contratado */
					List<MbcEscalaProfUnidCirg> curEpuAnc  = this.getMbcEscalaProfUnidCirgDAO().pesquisarEscalaProfissionalPorFuncao(curCrg.getSalaCirurgica().getId().getUnfSeq(), curCrg.getSalaCirurgica().getId().getSeqp(), diaSemana, turno, DominioFuncaoProfissional.ANC);
					if(curEpuAnc != null && !curEpuAnc.isEmpty()){

						MbcProfCirurgias profCirurgia = mbcControleEscalaCirurgicaRN.populaMbcProfCirurgias(curCrg, curEpuAnc.get(0));
						/* INSERT INTO	mbc_prof_cirurgias */
						this.getMbcProfCirurgiasRN().inserirMbcProfCirurgias(profCirurgia);
					}
				}
			}
		}
	}
	
	/** 
	 * ORADB PROCEDURE mbck_mbc_rn.RN_MBCP_ATU_INS_ATD
	 * @param escalaCirurgica
	 * @throws BaseException
	 */
	public Integer inserirAtendimentoCirurgiaAmbulatorio(AghAtendimentos atendimento, AipPacientes paciente, Date dataInicioCirurgia, AghUnidadesFuncionais unidadeFuncional, AghEspecialidades especialidade, RapServidores servidorMovimento) throws BaseException {

		Integer clinicaPac = null;
		Integer prontuario = null;
		Integer clinicaPed = null;
		Integer atdSeq = null;

		if (especialidade != null) {

			if (especialidade.getClinica() != null) {
				
				clinicaPac = especialidade.getClinica().getCodigo();
			
			} else {
				
				throw new ApplicationBusinessException(MbcControleEscalaCirurgicaRNExceptionCode.AGH_00163);
			
			}
			
			if (paciente != null) {
			
				prontuario = paciente.getProntuario();
			
			} else {
				
				throw new ApplicationBusinessException(MbcControleEscalaCirurgicaRNExceptionCode.AIP_00013);
			
			}

			final AghParametros aghParametros = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CLINICA_PEDIATRIA);

			if (aghParametros != null && aghParametros.getVlrNumerico() != null) {

				clinicaPed = aghParametros.getVlrNumerico().intValue();

				atendimento = new AghAtendimentos();
				atendimento.setPaciente(paciente);
				atendimento.setDthrInicio(dataInicioCirurgia);

				/*  decode(v_clinica_pac, v_clinica_ped, 'S', 'N')  */
				if(clinicaPac == clinicaPed){
					atendimento.setIndPacPediatrico(Boolean.TRUE);
				}else{
					atendimento.setIndPacPediatrico(Boolean.FALSE);
				}
				atendimento.setIndPacPrematuro(Boolean.FALSE);
				atendimento.setIndPacAtendimento(DominioPacAtendimento.S);

				atendimento.setUnidadeFuncional(unidadeFuncional);
				atendimento.setEspecialidade(especialidade);
				atendimento.setServidor(servidorLogadoFacade.obterServidorLogado());
				atendimento.setServidorMovimento(servidorMovimento);
				atendimento.setProntuario(prontuario);
				atendimento.setOrigem(DominioOrigemAtendimento.C);

				/**
				 * Insere em AghAtendimentos
				 */
				getPacienteFacade().inserirAtendimento(atendimento, null);
				
				if (atendimento != null) {
					
					atdSeq = atendimento.getSeq();
					
				}

			} else {
				throw new ApplicationBusinessException(MbcControleEscalaCirurgicaRNExceptionCode.AGH_00182);
			}
						
		}
		
		return atdSeq;
		
	}
	
	protected MbcEscalaProfUnidCirgDAO getMbcEscalaProfUnidCirgDAO() {
		return mbcEscalaProfUnidCirgDAO;
	}
	
	protected MbcProfCirurgiasRN getMbcProfCirurgiasRN() {
		return mbcProfCirurgiasRN;
	}
	
	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return iPacienteFacade;
	}

}
