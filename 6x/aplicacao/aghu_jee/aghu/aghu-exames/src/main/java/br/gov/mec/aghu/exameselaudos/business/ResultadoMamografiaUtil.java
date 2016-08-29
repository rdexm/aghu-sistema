package br.gov.mec.aghu.exameselaudos.business;

import java.io.Serializable;

import javax.ejb.Stateless;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ResultadoMamografiaUtil implements Serializable{

	private static final long serialVersionUID = 6116321235011574362L;
	
	public void incluirException(ConsisteDadosRNExceptionCode code, BaseListException listaException) {
		   listaException.add(new ApplicationBusinessException(code));
	}

	public enum ConsisteDadosRNExceptionCode implements BusinessExceptionCode {
		AEL_03387,		AEL_03388,		AEL_03245,		AEL_03385,		
		AEL_03246,		AEL_03247,		AEL_03238,		AEL_03383,				
		AEL_03252,		AEL_03255,		AEL_03258,		AEL_03250,
		AEL_03253,		AEL_03256,		AEL_03259,		AEL_03251,
		AEL_03254,		AEL_03257,		AEL_03260,		AEL_03280,
		AEL_03283,		AEL_03286,		AEL_03281,		AEL_03284,
		AEL_03287,		AEL_03282,		AEL_03285,		AEL_03288,
		AEL_03298,		AEL_03299,		AEL_03300,		AEL_03301,
		AEL_03302,		AEL_03303,		AEL_03304,		AEL_03305,
		AEL_03319,		AEL_03239,		AEL_03384,		AEL_03386,
		AEL_03222,		AEL_03261,		AEL_03264,		AEL_03267,
		AEL_03270,		AEL_03262,		AEL_03265,		AEL_03268,
		AEL_03271,		AEL_03263,		AEL_03266,		AEL_03269,
		AEL_03272,		AEL_03289,		AEL_03292,		AEL_03295,
		AEL_03290,		AEL_03293,		AEL_03296,		AEL_03291,
		AEL_03294,		AEL_03297,		AEL_03306,		AEL_03307,
		AEL_03308,		AEL_03309,		AEL_03310,		AEL_03311,
		AEL_03312,		AEL_03313,		AEL_03321,		AEL_03328,
		AEL_03216;
	}

	public boolean validarSeNumFilmes(Short numFilme) {
		
		boolean retorno = false;
		
		if(numFilme != null && numFilme >= 1 &&  numFilme <= 99) {
			retorno = true;
		}
			
		return retorno;
	}
	
}
